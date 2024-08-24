package com.cyd.gameserver.bolt.broker.server.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerClientModulesAware;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerServerAware;
import com.cyd.gameserver.bolt.broker.server.balanced.BrokerClientProxy;
import com.cyd.gameserver.bolt.broker.server.service.BrokerClientModules;
import com.cyd.gameserver.bolt.core.client.BrokerClientType;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import com.cyd.gameserver.common.consts.LogName;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Consumer;

@Slf4j(topic = LogName.CommonStdout)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterBrokerClientModuleMessageBrokerProcessor extends AsyncUserProcessor<BrokerClientModuleMessage> implements BrokerServerAware, BrokerClientModulesAware {

    @Setter
    BrokerServer brokerServer;

    @Setter
    BrokerClientModules brokerClientModules;

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, BrokerClientModuleMessage brokerClientModuleMessage) {
        brokerClientModules.add(brokerClientModuleMessage);
        brokerClientModuleMessage.setAddress(bizContext.getRemoteAddress());

        val balancedManager = brokerServer.getBalancedManager();
        balancedManager.register(brokerClientModuleMessage);

        if (GlobalConfig.isSendBrokerClientModuleMessage()) {
            sendBrokerClientModuleMessage(brokerClientModuleMessage);
        }
    }

    /**
     * 发送逻辑服信息给其他逻辑服
     */
    private void sendBrokerClientModuleMessage(BrokerClientModuleMessage brokerClientModuleMessage) {
        if (BrokerClientType.LOGIC == brokerClientModuleMessage.getBrokerClientType()) {
            extractedLogic(brokerClientModuleMessage);
        }
        if (BrokerClientType.EXTERNAL == brokerClientModuleMessage.getBrokerClientType()) {
            extractedExternal(brokerClientModuleMessage);
        }
    }

    /**
     * 将所有逻辑服信息发送给目标对外服
     *
     * @param brokerClientModuleMessage
     */
    private void extractedExternal(BrokerClientModuleMessage brokerClientModuleMessage) {
        Consumer<BrokerClientModuleMessage> consumer = message -> {
            try {
                brokerServer.getRpcServer().oneway(brokerClientModuleMessage.getAddress(), message);
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        };

        brokerClientModules.list()
                .stream()
                .filter(moduleMessage -> BrokerClientType.LOGIC == moduleMessage.getBrokerClientType())
                .forEach(consumer);
    }

    /**
     * 将目标逻辑服信息发送给所有对外服
     *
     * @param brokerClientModuleMessage
     */
    private void extractedLogic(BrokerClientModuleMessage brokerClientModuleMessage) {
        Consumer<BrokerClientProxy> consumer = proxy -> {
            try {
                proxy.oneway(brokerClientModuleMessage);
            } catch (RemotingException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        };

        brokerServer
                .getBalancedManager()
                .getExternalBalanced()
                .list()
                .forEach(consumer);
    }

    @Override
    public String interest() {
        return BrokerClientModuleMessage.class.getName();
    }
}
