package com.cyd.gameserver.external.core.kit.hook;

import com.cyd.gameserver.external.core.micro.session.UserSession;

/**
 * 用户钩子，上下线时触发
 */
public interface UserHook {

    /**
     *用户上线触发
     * @param session
     */
    void into(UserSession session);

    /**
     * 用户下线触发
     * @param userSession
     */
    void quit(UserSession userSession);
}
