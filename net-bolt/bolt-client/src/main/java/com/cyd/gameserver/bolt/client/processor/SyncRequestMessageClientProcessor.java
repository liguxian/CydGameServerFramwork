package com.cyd.gameserver.bolt.client.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;
import com.cyd.gameserver.action.skeleton.core.flow.attr.FlowAttr;
import com.cyd.gameserver.action.skeleton.protocol.SyncRequestMessage;
import com.cyd.gameserver.bolt.core.aware.BrokerClientAware;
import com.cyd.gameserver.bolt.core.aware.UserProcessorExecutorAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.message.RequestBrokerClientModuleMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

/**
 * 网关转发对外服给逻辑服的消息到逻辑服的消息处理器(用来处理，游戏逻辑服与同类型多个游戏逻辑服通信请求的)
 */
@Slf4j
public class SyncRequestMessageClientProcessor extends SyncUserProcessor<SyncRequestMessage> implements BrokerClientAware, UserProcessorExecutorAware {

    BrokerClient brokerClient;

    Executor userProcessorExecutor;

    @Override
    public Object handleRequest(BizContext bizContext, SyncRequestMessage request) {
        /*
         * 多次访问的变量，保存到局部变量，可以提升性能。
         * 把成员变量的访问变为局部变量的访问 。 通过栈帧访问（线程栈），不用每次从堆中得到成员变量
         *
         * 因为这段代码访问频繁，才这样做。常规下不需要这么做
         * 可以参考 HashMap 的 putVal 方法相关
         */
        final BrokerClient brokerClient = this.brokerClient;

        // 得到逻辑服对应的业务框架
        BarSkeleton barSkeleton = brokerClient.getBarSkeleton();

        // 业务框架 flow 上下文
        FlowContext flowContext = barSkeleton
                // 业务框架 flow 上下文 工厂
                .getFlowContextFactory()
                // 创建 flow 上下文
                .createFlowContext();

        // 设置请求参数
        flowContext.setRequest(request);
        // 不需要业务框架来发送消息，由消息处理器来发送
        flowContext.setExecuteActionAfter(false);

        // 动态属性添加
        flowContext.option(FlowAttr.brokerClientContext, brokerClient);
        flowContext.option(FlowAttr.logicServerId, brokerClient.getId());
        flowContext.option(FlowAttr.logicServerTag, brokerClient.getTag());

        /*
         * 如果是同步的，就不支持线程编排了；
         * 使用默认线程就可以，通常这个消息处理器是用来处理
         * （游戏逻辑服与同类型多个游戏逻辑服通信请求的）
         */
        barSkeleton.handle(flowContext);

        // action 方法返回值是 void 的，不做处理
        ActionCommand actionCommand = flowContext.getActionCommand();
        if (actionCommand.getActionMethodReturnInfo().isVoid()) {
            return null;
        }

        return flowContext.getResponse();
    }

    @Override
    public void setBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public String interest() {
        return SyncRequestMessage.class.getName();
    }

    @Override
    public void setUserProcessorExecutor(Executor executor) {
        this.userProcessorExecutor = executor;
    }

    @Override
    public Executor getUserProcessorExecutor() {
        return this.userProcessorExecutor;
    }

    @Override
    public Executor getExecutor() {
        return this.userProcessorExecutor;
    }
}
