package com.cyd.gameserver.bolt.broker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.Connection;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcServer;
import com.cyd.gameserver.action.skeleton.core.exception.ActionErrorEnum;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerServerAware;
import com.cyd.gameserver.bolt.broker.server.balanced.BalancedManager;
import com.cyd.gameserver.bolt.broker.server.balanced.BrokerClientProxy;
import com.cyd.gameserver.bolt.broker.server.balanced.ExternalBrokerClientLoadBalanced;
import com.cyd.gameserver.bolt.core.aware.BrokerClientAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.common.AbstractAsyncUserProcessor;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.RequestBrokerClientModuleMessage;
import com.cyd.gameserver.common.consts.LogName;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Objects;

/**
 * 网关接收对外服消息，转发给逻辑服
 */
@Slf4j(topic = LogName.MsgTransferTopic)
public class RequestMessageBrokerProcessor extends AbstractAsyncUserProcessor<RequestMessage> implements BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, RequestMessage request) {
        if(GlobalConfig.requestResponseLog) {
            extractedPrint(request);
        }

        //找到对应的逻辑服代理，转发信息
        val logicBalanced = brokerServer.getBalancedManager().getLogicBalanced();
        val headMetadata = request.getHeadMetadata();

        val brokerClientRegion = logicBalanced.getBrokerClientRegion(headMetadata.getCmdMerge());
        if (brokerClientRegion == null) {
            //  通知对外服， 路由不存在
            extractedNotRoute(bizContext, request);
            return;
        }

        //设置进程号
        headMetadata.setWithNo(brokerServer.getWithNo());

        BrokerClientProxy brokerClientProxy = brokerClientRegion.getBrokerClientProxy(headMetadata);
        if (brokerClientRegion == null) {
            //  通知对外服， 路由不存在
            extractedNotRoute(bizContext, request);
            return;
        }

        try{
            brokerClientProxy.oneway(request);
        } catch (RemotingException | InterruptedException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }

    }

    private void extractedPrint(RequestMessage request) {

        log.info("游戏网关把对外服 请求 转发到逻辑服 : {}", request);

        BalancedManager balancedManager = brokerServer.getBalancedManager();
        ExternalBrokerClientLoadBalanced externalLoadBalanced = balancedManager.getExternalBalanced();

        for (BrokerClientProxy brokerClientProxy : externalLoadBalanced.list()) {
            log.info("brokerClientProxy : {}", brokerClientProxy);
        }
    }

    private void extractedNotRoute(BizContext bizCtx, RequestMessage requestMessage) {
        // 路由不存在
        Connection connection = bizCtx.getConnection();
        ResponseMessage responseMessage = requestMessage.createResponseMessage();
        HeadMetadata headMetadata = requestMessage.getHeadMetadata();

        ActionErrorEnum errorCode = ActionErrorEnum.cmdInfoErrorCode;
        if (headMetadata.getOther() instanceof ActionErrorEnum theCode) {
            errorCode = theCode;
        }

        responseMessage.setValidatorMsg(errorCode.getMsg())
                .setResponseStatus(errorCode.getCode());

        RpcServer rpcServer = brokerServer.getRpcServer();

        try {
            rpcServer.oneway(connection, responseMessage);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String interest() {
        return RequestMessage.class.getName();
    }
}
