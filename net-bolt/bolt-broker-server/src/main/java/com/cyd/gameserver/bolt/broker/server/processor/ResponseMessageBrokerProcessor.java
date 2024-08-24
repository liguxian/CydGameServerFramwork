package com.cyd.gameserver.bolt.broker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerServerAware;
import com.cyd.gameserver.bolt.broker.server.balanced.BalancedManager;
import com.cyd.gameserver.bolt.broker.server.balanced.BrokerClientProxy;
import com.cyd.gameserver.bolt.broker.server.balanced.ExternalBrokerClientLoadBalanced;
import com.cyd.gameserver.bolt.core.common.AbstractAsyncUserProcessor;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 把逻辑服的响应转发给对外服
 */
@Slf4j
public class ResponseMessageBrokerProcessor extends AbstractAsyncUserProcessor<ResponseMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, ResponseMessage responseMessage) {
        if (GlobalConfig.requestResponseLog) {
            log.info("把逻辑服的响应转发到对外服 {}", responseMessage);
        }
        HeadMetadata headMetadata = responseMessage.getHeadMetadata();
        int sourceClientId = headMetadata.getSourceClientId();

        BalancedManager balancedManager = brokerServer.getBalancedManager();
        ExternalBrokerClientLoadBalanced externalBalanced = balancedManager.getExternalBalanced();
        BrokerClientProxy brokerClientProxy = externalBalanced.get(sourceClientId);

        if(Objects.isNull(brokerClientProxy)) {
            log.warn("对外服不存在: [{}]", sourceClientId);
            return;
        }

        try {
            brokerClientProxy.oneway(responseMessage);
        } catch (RemotingException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return ResponseMessage.class.getName();
    }
}
