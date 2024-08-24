package com.cyd.gameserver.external.core.netty;

import com.cyd.gameserver.bolt.client.BrokerClientApplication;
import com.cyd.gameserver.bolt.core.client.BrokerAddress;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import com.cyd.gameserver.external.core.ExternalCore;
import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.ExternalServer;
import com.cyd.gameserver.external.core.broker.client.ExternalBrokerClientStartup;
import com.cyd.gameserver.external.core.micro.MicroBootstrap;
import com.cyd.gameserver.external.core.micro.join.ExternalJoinSelector;
import com.cyd.gameserver.external.core.micro.join.ExternalJoinSelectors;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.ServiceLoader;

@FieldDefaults(level = AccessLevel.PRIVATE)
public final class DefaultExternalServer implements ExternalServer {

    ExternalCore core;
    DefaultExternalCoreSetting setting;

    /** 内部逻辑服 连接网关服务器，与网关通信 */
    ExternalBrokerClientStartup externalBrokerClientStartup;

    /**
     * 网关地址
     */
    BrokerAddress brokerAddress;

    public DefaultExternalServer(ExternalCore core, DefaultExternalCoreSetting setting, BrokerAddress brokerAddress, ExternalBrokerClientStartup externalBrokerClientStartup) {
        this.core = core;
        this.setting = setting;
        this.brokerAddress = brokerAddress;
        this.externalBrokerClientStartup = externalBrokerClientStartup;
    }

    static {
        ServiceLoader<ExternalJoinSelector> serviceLoader = ServiceLoader.load(ExternalJoinSelector.class);
        serviceLoader.forEach(ExternalJoinSelectors::putIfAbsent);
    }

    @Override
    public void startUp() {
        //创建与用户连接的服务器
        MicroBootstrap microBootstrap = this.core.createMicroBootstrap();

        var startExternalBrokerClient = System.getProperty("ExternalBrokerClientStartup", "true");
        if (Boolean.parseBoolean(startExternalBrokerClient)) {
            // 启动与 Broker 游戏网关通信的 BrokerClient
            startExternalBrokerClient();
        }

        //注入setting中对应的属性到对应的属性
        setting.inject();

        microBootstrap.startUp();
    }

    /**
     * 连接网关
     */
    private void startExternalBrokerClient() {
        BrokerClientBuilder brokerClientBuilder = BrokerClientApplication.initConfig(this.externalBrokerClientStartup);
        brokerClientBuilder.awareInject(setting);
        if(Objects.nonNull(brokerAddress)) {
            brokerClientBuilder.brokerAddress(brokerAddress);
        }
        BrokerClient brokerClient = BrokerClientApplication.start(brokerClientBuilder);
        setting.setBrokerClient(brokerClient);
    }

    public static DefaultExternalServerBuilder builder(int externalPort){
        return new DefaultExternalServerBuilder(externalPort);
    }
}
