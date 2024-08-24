package com.cyd.gameserver.bolt.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContextKit;
import com.cyd.gameserver.action.skeleton.core.flow.attr.FlowAttr;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.bolt.client.action.skeleton.BoltChannelContext;
import com.cyd.gameserver.bolt.core.aware.BrokerClientAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.common.AbstractAsyncUserProcessor;
import com.cyd.gameserver.bolt.core.common.processor.hook.ClientProcessorHooks;
import com.cyd.gameserver.bolt.core.common.processor.hook.RequestMessageClientProcessorHook;
import com.cyd.gameserver.bolt.core.message.RequestBrokerClientModuleMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 逻辑服接收网关消息处理器
 */
@Slf4j
public class RequestMessageClientProcessor extends AbstractAsyncUserProcessor<RequestMessage> implements BrokerClientAware {

    BrokerClient brokerClient;

    RequestMessageClientProcessorHook requestMessageClientProcessorHook;

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, RequestMessage request) {
        try {
            /*
             * 多次访问的变量，保存到局部变量，可以提升性能。
             * 把成员变量的访问变为局部变量的访问 。 通过栈帧访问（线程栈），不用每次从堆中得到成员变量
             *
             * 因为这段代码访问频繁，才这样做。常规下不需要这么做
             * 可以参考 HashMap 的 putVal 方法相关
             */
            final BrokerClient brokerClient = this.brokerClient;

            //创建请求上下文
            BarSkeleton barSkeleton = brokerClient.getBarSkeleton();
            FlowContext flowContext = barSkeleton.getFlowContextFactory().createFlowContext();

            flowContext.setRequest(request)
                    .setBarSkeleton(barSkeleton);

            //动态属性添加
            BoltChannelContext boltChannelContext = new BoltChannelContext(asyncContext);
            flowContext.option(FlowAttr.channelContext, boltChannelContext);
            flowContext.option(FlowAttr.brokerClientContext, brokerClient);
            flowContext.option(FlowAttr.logicServerId, brokerClient.getId());
            flowContext.option(FlowAttr.logicServerTag, brokerClient.getTag());

            //设置flowContext的一些属性
            FlowContextKit.employ(flowContext);

            requestMessageClientProcessorHook.processLogic(barSkeleton, flowContext);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    @Override
    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;

        ClientProcessorHooks clientProcessorHooks = brokerClient.getClientProcessorHooks();
        requestMessageClientProcessorHook = clientProcessorHooks.getRequestMessageClientProcessorHook();
    }

    @Override
    public String interest() {
        return RequestMessage.class.getName();
    }
}
