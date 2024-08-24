package com.cyd.gameserver.external.core.netty;

import com.cyd.gameserver.bolt.core.aware.BrokerClientAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import com.cyd.gameserver.common.kit.attr.AttrOptions;
import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.aware.ExternalCoreSettingAware;
import com.cyd.gameserver.external.core.aware.UserSessionsAware;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.kit.hook.IdleProcessSetting;
import com.cyd.gameserver.external.core.kit.hook.UserHook;
import com.cyd.gameserver.external.core.micro.MicroBootstrap;
import com.cyd.gameserver.external.core.micro.MicroBootstrapFlow;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashSet;

import java.util.Set;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultExternalCoreSetting implements ExternalCoreSetting {

    //动态属性
    final AttrOptions options = new AttrOptions();

    final Set<Object> injectObj = new NonBlockingHashSet();

    //连接方式tcp udp websocket 默认tcp
    @Setter
    ExternalJoinEnum joinEnum = ExternalJoinEnum.TCP;

    //客户连接端口
    @Setter
    int port;

    //与用户连接的服务器
    MicroBootstrap microBootstrap;

    //netty服务器handler流
    MicroBootstrapFlow<?> microBootstrapFlow;

    // 心跳相关的设置
    IdleProcessSetting idleProcessSetting;

    //用户session管理器
    UserSessions<?,?> userSessions;

    //用户上下线钩子
    UserHook userHook;

    @Setter
    //网关客户端
    BrokerClient brokerClient;

    //将其他变量对象注入给一个变量对象
    public void inject() {
        injectObj.forEach(this::aware);
    }

    @Override
    public void aware(Object obj) {
        if (obj instanceof ExternalCoreSettingAware aware) {
            aware.setExternalCoreSetting(this);
        }

        if (obj instanceof UserSessionsAware aware) {
            aware.setUserSessions(userSessions);
        }

        if (obj instanceof BrokerClientAware aware) {
            aware.setBrokerClient(brokerClient);
        }
    }

    public <T> MicroBootstrapFlow<T> getMicroBootstrapFlow() {
        return (MicroBootstrapFlow<T>) microBootstrapFlow;
    }

    public void setMicroBootstrap(MicroBootstrap bootstrap) {
        microBootstrap = bootstrap;
        injectObj.add(microBootstrap);
    }

    public void setMicroBootstrapFlow(MicroBootstrapFlow<?> bootstrapFlow) {
        microBootstrapFlow = bootstrapFlow;
        injectObj.add(bootstrapFlow);
    }

    public void setIdleProcessSetting(IdleProcessSetting idleProcessSetting){
        this.idleProcessSetting = idleProcessSetting;
        injectObj.add(this.idleProcessSetting);
    }

    public void setUserSessions(UserSessions<?,?> userSessions) {
        this.userSessions = userSessions;
        injectObj.add(this.userSessions);
    }

    public void setUserHook(UserHook userHook) {
        this.userHook = userHook;
        injectObj.add(this.userHook);
    }
}
