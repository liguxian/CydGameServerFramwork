package com.cyd.gameserver.external.core.broker.client;

import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.BarSkeletonBuilder;
import com.cyd.gameserver.bolt.client.AbstractBrokerClientStartup;
import com.cyd.gameserver.bolt.client.processor.RequestBrokerClientModuleMessageClientProcessor;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import com.cyd.gameserver.bolt.core.client.BrokerClientType;
import com.cyd.gameserver.external.core.broker.client.processor.BrokerClientModuleMessageExternalProcessor;
import com.cyd.gameserver.external.core.broker.client.processor.ResponseMessageExternalProcessor;
import lombok.Setter;

/**
 * 对外服与网关通讯的client启动器
 */
public class ExternalBrokerClientStartup extends AbstractBrokerClientStartup {
    @Setter
    String id;

    /**
     * 床架业务框架
     * @return
     */
    @Override
    public BarSkeleton createBarSkeleton() {
        // 对外服不需要业务框架，这里给个空的
        BarSkeletonBuilder builder = BarSkeleton.newBuilder();
        return builder.build();
    }

    @Override
    public BrokerClientBuilder createBrokerClientBuilder() {

        return BrokerClient.builder()
                .id(id)
                .appName("游戏对外服")
                .tag("external")
                .brokerClientType(BrokerClientType.EXTERNAL)
                ;
    }

    @Override
    public void registerUserProcessor(BrokerClientBuilder brokerClientBuilder) {
        brokerClientBuilder
                .registerUserProcessor(RequestBrokerClientModuleMessageClientProcessor::new)
                //接收网关信息
                .registerUserProcessor(ResponseMessageExternalProcessor::new)
                //逻辑服上线处理器
                .registerUserProcessor(BrokerClientModuleMessageExternalProcessor::new)
        ;
    }
}
