package com.cyd.gameserver.bolt.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.cyd.gameserver.action.skeleton.toy.IoGameBanner;
import com.cyd.gameserver.bolt.core.aware.BrokerClientItemAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientItem;
import com.cyd.gameserver.bolt.core.client.BrokerClientItemManager;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.RequestBrokerClientModuleMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 网关请求逻辑服模块信息消息的客户端处理器
 */
@Slf4j
public class RequestBrokerClientModuleMessageClientProcessor extends AsyncUserProcessor<RequestBrokerClientModuleMessage> implements BrokerClientItemAware {

    @Setter
    BrokerClientItem brokerClientItem;

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, RequestBrokerClientModuleMessage request) {
        if (GlobalConfig.requestResponseLog) {
            log.info("bizCtx.getRemoteAddress() : {}", bizContext.getRemoteAddress());
        }

        //设置网关进程号
        brokerClientItem.setBrokerServerWithNo(request.getWithNo());
        // 客户端服务器注册到游戏网关服
        brokerClientItem.registerToBroker();

        if (GlobalConfig.requestResponseLog) {
            BrokerClient brokerClient = brokerClientItem.getBrokerClient();
            BrokerClientItemManager brokerClientManager = brokerClient.getBrokerClientItemManager();
            log.info("brokerClientItems : {}", brokerClientManager.countActiveItem());
        }

        IoGameBanner.me().countDown();
    }

    @Override
    public String interest() {
        return RequestBrokerClientModuleMessage.class.getName();
    }
}
