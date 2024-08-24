package com.cyd.gameserver.bolt.client;

import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.GlobalSetting;
import com.cyd.gameserver.bolt.core.client.BrokerAddress;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.common.kit.NetworkKit;

public sealed interface BrokerClientStartup permits AbstractBrokerClientStartup {

    /**
     * 创建业务框架
     */
    BarSkeleton createBarSkeleton();

    /**
     * 创建BrokerClientBuilder
     */
    BrokerClientBuilder createBrokerClientBuilder();

    /**
     * 创建BrokerAddress
     */
    default BrokerAddress createBrokerAddress() {
        String localIp = NetworkKit.LOCAL_IP;
        int port = GlobalConfig.brokerPort;
        return new BrokerAddress(localIp, port);
    }

    /**
     * 添加连接处理器
     */
    void connectionEventProcessor(BrokerClientBuilder brokerClientBuilder);

    /**
     * 注册用户处理器
     */
    void registerUserProcessor(BrokerClientBuilder brokerClientBuilder);
}
