package com.cyd.gameserver.bolt.client;

import com.alipay.remoting.rpc.RpcConfigs;
import com.cyd.gameserver.action.skeleton.toy.IoGameBanner;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import lombok.experimental.UtilityClass;

/**
 * BrokerClient 构建与启动
 */
@UtilityClass
public class BrokerClientApplication {

    public BrokerClientBuilder initConfig(AbstractBrokerClientStartup startup) {
        return startup.initConfig();
    }

    public BrokerClient start(BrokerClientBuilder builder) {
        System.setProperty(RpcConfigs.DISPATCH_MSG_LIST_IN_DEFAULT_EXECUTOR, "false");
        BrokerClient brokerClient = builder.build();
        brokerClient.init();

        IoGameBanner.render();

        return brokerClient;
    }

    public BrokerClient start(AbstractBrokerClientStartup startup) {
        BrokerClientBuilder brokerClientBuilder = initConfig(startup);
        BrokerClient brokerClient = start(brokerClientBuilder);

        return brokerClient;
    }
}
