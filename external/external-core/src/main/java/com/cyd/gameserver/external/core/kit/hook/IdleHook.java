package com.cyd.gameserver.external.core.kit.hook;

import com.cyd.gameserver.external.core.micro.session.UserSession;

/**
 * 心跳钩子
 */
public interface IdleHook<IdleEvent> {

    /**
     * 心跳回调事件
     */
    boolean callback(UserSession userSession, IdleEvent idleEvent);
}
