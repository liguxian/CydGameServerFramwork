package com.cyd.gameserver.external.core.netty.kit;

import com.cyd.gameserver.bolt.core.client.BrokerAddress;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.external.core.ExternalServer;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.netty.DefaultExternalServer;
import com.cyd.gameserver.external.core.netty.DefaultExternalServerBuilder;
import lombok.experimental.UtilityClass;

/**
 * 对外服创建工具
 */
@UtilityClass
public class ExternalServerCreateKit {

    public ExternalServer createExternalServer(int externalPort, ExternalJoinEnum joinEnum){
        return newBuilder(externalPort, joinEnum).build();
    }

    public DefaultExternalServerBuilder newBuilder(int externalPort, ExternalJoinEnum joinEnum){
        return DefaultExternalServer.builder(externalPort)
                .externalJoinEnum(joinEnum)
                //网关的链接地址
                .setBrokerAddress(new BrokerAddress("127.0.0.1", GlobalConfig.brokerPort))
                ;
    }
}
