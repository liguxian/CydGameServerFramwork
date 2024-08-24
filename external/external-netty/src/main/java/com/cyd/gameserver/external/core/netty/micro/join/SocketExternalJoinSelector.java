package com.cyd.gameserver.external.core.netty.micro.join;

import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.kit.hook.IdleProcessSetting;
import com.cyd.gameserver.external.core.micro.MicroBootstrap;
import com.cyd.gameserver.external.core.micro.join.ExternalJoinSelector;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import com.cyd.gameserver.external.core.netty.DefaultExternalCoreSetting;
import com.cyd.gameserver.external.core.netty.SettingOption;
import com.cyd.gameserver.external.core.netty.handler.SocketIdleHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketRequestBrokerHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketUserSessionHandler;
import com.cyd.gameserver.external.core.netty.hook.DefaultSocketIdleHook;
import com.cyd.gameserver.external.core.netty.micro.SocketMicroBootstrap;
import com.cyd.gameserver.external.core.netty.micro.session.SocketUserSessions;

import java.util.Objects;

public abstract class SocketExternalJoinSelector implements ExternalJoinSelector {

    @Override
    public void defaultSetting(ExternalCoreSetting coreSetting) {
        DefaultExternalCoreSetting setting = (DefaultExternalCoreSetting) coreSetting;

        // microBootstrap；如果开发者没有手动赋值，则根据当前连接方式生成
        MicroBootstrap microBootstrap = setting.getMicroBootstrap();
        if(Objects.isNull(microBootstrap)) {
            SocketMicroBootstrap socketMicroBootstrap = new SocketMicroBootstrap();
            setting.setMicroBootstrap(socketMicroBootstrap);

            socketMicroBootstrap.setSetting(setting);
        }

        //用户session管理器：若开发者没有手动赋值，则根据当前连接方式生成
        UserSessions<?, ?> userSessions = setting.getUserSessions();
        if(Objects.isNull(userSessions)) {
            userSessions = new SocketUserSessions();
            setting.setUserSessions(userSessions);
        }

        //若开启了心跳，没有钩子则强制给一个
        IdleProcessSetting idleProcessSetting = setting.getIdleProcessSetting();
        if(Objects.nonNull(idleProcessSetting)) {
            if(Objects.isNull(idleProcessSetting.getIdleHook())) {
                idleProcessSetting.setIdleHook(new DefaultSocketIdleHook());
            }

            //心跳钩子 handler
            setting.option(SettingOption.socketIdleHandler, new SocketIdleHandler());
        }

        setting.option(SettingOption.socketUserSessionHandler, new SocketUserSessionHandler());
        setting.option(SettingOption.socketRequestBrokerHandler, new SocketRequestBrokerHandler());
    }
}
