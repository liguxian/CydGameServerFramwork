package com.cyd.gameserver.external.core.netty;

import com.cyd.gameserver.common.kit.attr.AttrOption;
import com.cyd.gameserver.external.core.netty.handler.SocketIdleHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketRequestBrokerHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketUserSessionHandler;

/**
 * ExternalCoreSetting的动态属性
 */
public interface SettingOption {

    AttrOption<SocketUserSessionHandler> socketUserSessionHandler =
            AttrOption.valueOf("SocketUserSessionHandler");

    //    AttrOption<SocketCmdAccessAuthHandler> socketCmdAccessAuthHandler =
//            AttrOption.valueOf("SocketCmdAccessAuthHandler");
//
    AttrOption<SocketRequestBrokerHandler> socketRequestBrokerHandler =
            AttrOption.valueOf("SocketRequestBrokerHandler");

    AttrOption<SocketIdleHandler> socketIdleHandler = AttrOption.valueOf("SocketIdleHandler");
}
