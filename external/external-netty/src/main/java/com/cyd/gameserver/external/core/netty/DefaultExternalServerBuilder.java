package com.cyd.gameserver.external.core.netty;

import com.cyd.gameserver.bolt.core.client.BrokerAddress;
import com.cyd.gameserver.external.core.ExternalCore;
import com.cyd.gameserver.external.core.broker.client.ExternalBrokerClientStartup;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.kit.hook.IdleProcessSetting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultExternalServerBuilder {

    final DefaultExternalCoreSetting setting = new DefaultExternalCoreSetting();

    /** 内部逻辑服 连接网关服务器，与网关通信 */
    ExternalBrokerClientStartup externalBrokerClientStartup = new ExternalBrokerClientStartup();

    /**
     * 网关地址
     */
    BrokerAddress brokerAddress;

    public DefaultExternalServerBuilder(int externalPort) {
        setting.setPort(externalPort);
    }

    //用户连接netty方式
    public DefaultExternalServerBuilder externalJoinEnum(ExternalJoinEnum joinEnum) {
        setting.setJoinEnum(joinEnum);
        return this;
    }

    //是否开启心跳检测
    public DefaultExternalServerBuilder enableIdle(IdleProcessSetting idleProcessSetting) {
        setting.setIdleProcessSetting(idleProcessSetting);
        return this;
    }

    public DefaultExternalServer build() {
        check();

        ExternalCore core = new DefaultExternalCore(setting);
        return new DefaultExternalServer(core, setting, brokerAddress, externalBrokerClientStartup);
    }

    public void check() {

    }
}
