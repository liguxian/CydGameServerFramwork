package com.cyd.gameserver.bolt.broker.server.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.exception.RemotingException;
import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerServerAware;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.RequestBrokerClientModuleMessage;
import com.cyd.gameserver.common.consts.LogName;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j(topic = LogName.ConnectionTopic)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectionEventBrokerProcessor implements ConnectionEventProcessor, BrokerServerAware {

    @Setter
    BrokerServer brokerServer;

    Connection connection;

    String remoteAddress;

    final static RequestBrokerClientModuleMessage requestBrokerClientModuleMessage = new RequestBrokerClientModuleMessage();

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        extractedPrint(remoteAddress, connection);
        Objects.requireNonNull(remoteAddress);
        doCheckConnection(connection);

        this.connection = connection;
        this.remoteAddress = remoteAddress;

        //传网关进程号
        requestBrokerClientModuleMessage.setWithNo(brokerServer.getWithNo());

        //通知客户端发送模块信息
        try {
            brokerServer.getRpcServer().oneway(connection, requestBrokerClientModuleMessage);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
     }

    private static void extractedPrint(String remoteAddress, Connection conn) {
        if (GlobalConfig.openLog) {
            log.info("Broker ConnectionEventType:【{}】 remoteAddress:【{}】，Connection:【{}】",
                    ConnectionEventType.CONNECT, remoteAddress, conn
            );
        }
    }

    private void doCheckConnection(Connection conn) {
        Objects.requireNonNull(conn);
        Objects.requireNonNull(conn.getPoolKeys());
        Objects.requireNonNull(conn.getChannel());
        Objects.requireNonNull(conn.getUrl());
        Objects.requireNonNull(conn.getChannel().attr(Connection.CONNECTION).get());
    }
}
