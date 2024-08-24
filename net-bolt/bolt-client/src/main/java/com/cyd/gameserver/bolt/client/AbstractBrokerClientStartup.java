package com.cyd.gameserver.bolt.client;

import com.alipay.remoting.ConnectionEventType;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.toy.IoGameBanner;
import com.cyd.gameserver.bolt.client.processor.RequestBrokerClientModuleMessageClientProcessor;
import com.cyd.gameserver.bolt.client.processor.RequestMessageClientProcessor;
import com.cyd.gameserver.bolt.client.processor.SyncRequestMessageClientProcessor;
import com.cyd.gameserver.bolt.client.processor.connection.CloseConnectEventClientProcessor;
import com.cyd.gameserver.bolt.client.processor.connection.ConnectEventClientProcessor;
import com.cyd.gameserver.bolt.client.processor.connection.ConnectFailedEventClientProcessor;
import com.cyd.gameserver.bolt.client.processor.connection.ExceptionConnectEventClientProcessor;
import com.cyd.gameserver.bolt.core.GroupWith;
import com.cyd.gameserver.bolt.core.client.BrokerAddress;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract non-sealed class AbstractBrokerClientStartup implements BrokerClientStartup, GroupWith {

    /**
     * 网关地址
     */
    BrokerAddress brokerAddress;

    /**
     * BrokerClient构建器，如果没有赋值，使用子类的createBrokerClientBuilder创建一个
     *
     * @return
     */
    BrokerClientBuilder brokerClientBuilder;

    /**
     * 业务框架
     */
    BarSkeleton barSkeleton;

    /**
     * 进程组号
     */
    int withNo;

    /**
     * 添加连接事件
     * @param brokerClientBuilder
     */
    @Override
    public void connectionEventProcessor(BrokerClientBuilder brokerClientBuilder) {
        brokerClientBuilder
                .addConnectionEventProcessor(ConnectionEventType.CONNECT, ConnectEventClientProcessor::new)
                .addConnectionEventProcessor(ConnectionEventType.CONNECT_FAILED, ConnectFailedEventClientProcessor::new)
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, CloseConnectEventClientProcessor::new)
                .addConnectionEventProcessor(ConnectionEventType.EXCEPTION, ExceptionConnectEventClientProcessor::new)
        ;
    }

    /**
     * 注册用户事件
     * @param brokerClientBuilder
     */
    @Override
    public void registerUserProcessor(BrokerClientBuilder brokerClientBuilder) {
        brokerClientBuilder
                //网关请求客户端模块信息处理器
                .registerUserProcessor(RequestBrokerClientModuleMessageClientProcessor::new)
                //业务请求处理器
                .registerUserProcessor(RequestMessageClientProcessor::new)
                .registerUserProcessor(SyncRequestMessageClientProcessor::new)
                ;
    }

    public BrokerClientBuilder initConfig() {

        IoGameBanner.me();

        barSkeleton = createBarSkeleton();

        brokerAddress = createBrokerAddress();

        if (Objects.isNull(brokerClientBuilder)) {
            brokerClientBuilder = createBrokerClientBuilder();
        }

        Objects.requireNonNull(brokerClientBuilder, "BrokerClient 构建起必须要有");

        brokerClientBuilder
                .brokerAddress(brokerAddress)
                .barSkeleton(barSkeleton)
                .withNo(withNo);

        // 添加连接处理器
        this.connectionEventProcessor(this.brokerClientBuilder);
        // 注册用户处理器
        this.registerUserProcessor(this.brokerClientBuilder);

        return brokerClientBuilder;
    }
}
