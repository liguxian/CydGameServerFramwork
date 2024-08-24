package com.cyd.gameserver.bolt.broker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.exception.RemotingException;
import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerClientModulesAware;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerServerAware;
import com.cyd.gameserver.bolt.broker.server.balanced.BrokerClientProxy;
import com.cyd.gameserver.bolt.broker.server.balanced.region.BrokerClientRegion;
import com.cyd.gameserver.bolt.broker.server.service.BrokerClientModules;
import com.cyd.gameserver.bolt.core.client.BrokerClientType;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessageOffline;
import com.cyd.gameserver.bolt.core.message.RequestBrokerClientModuleMessage;
import com.cyd.gameserver.common.consts.LogName;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j(topic = LogName.ConnectionTopic)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloseConnectionEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware, BrokerClientModulesAware {

    @Setter
    BrokerServer brokerServer;

    @Setter
    BrokerClientModules brokerClientModules;

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        if (GlobalConfig.openLog) {
            log.info("Broker ConnectionEventType:【{}】 remoteAddress:【{}】，Connection:【{}】",
                    ConnectionEventType.CLOSE, remoteAddress, connection
            );
        }

        //移除客户端连接
        val balancedManager = brokerServer.getBalancedManager();
        val brokerClientProxy = balancedManager.remove(remoteAddress);

        //如果断开的是逻辑服，通知所有对外服有逻辑服下线了
        Optional.ofNullable(brokerClientProxy).ifPresent(proxy -> {
            val brokerClientModuleMessage = brokerClientModules.removeById(proxy.getId());
            val brokerClientType = brokerClientModuleMessage.getBrokerClientType();
            if(brokerClientType != BrokerClientType.LOGIC) {
                return;
            }

            Consumer<BrokerClientProxy> consumer = external -> {
                BrokerClientModuleMessageOffline offline = new BrokerClientModuleMessageOffline();
                offline.setBrokerClientModuleMessage(brokerClientModuleMessage);

                try {
                    external.oneway(offline);
                } catch (RemotingException | InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            };

            balancedManager
                    .getExternalBalanced()
                    .list()
                    .forEach(consumer);
        });
    }

    private static void extractedPrint(String remoteAddress, BrokerClientProxy brokerClientProxy) {
        if (GlobalConfig.openLog) {
            log.info("Broker ConnectionEventType:【{}】 remoteAddress:【{}】，brokerClientProxy:【{}】",
                    ConnectionEventType.CLOSE, remoteAddress, brokerClientProxy
            );
        }
    }
}
