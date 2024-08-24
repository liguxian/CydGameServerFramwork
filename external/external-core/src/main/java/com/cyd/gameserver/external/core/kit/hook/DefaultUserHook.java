package com.cyd.gameserver.external.core.kit.hook;

import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.external.core.aware.UserSessionsAware;
import com.cyd.gameserver.external.core.micro.session.UserSession;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = LogName.CommonStdout)
public class DefaultUserHook implements UserHook, UserSessionsAware {

    UserSessions<?,?> userSessions;

    @Override
    public void setUserSessions(UserSessions<?, ?> userSessions) {
        this.userSessions = userSessions;
    }

    @Override
    public void into(UserSession userSession) {
        long userId = userSession.getUserId();
        log.info("[玩家上线] 在线数量:{}  userId:{} -- {}"
                , userSessions.countOnline()
                , userId, userSession.getUserChannelId());
    }

    @Override
    public void quit(UserSession userSession) {
        long userId = userSession.getUserId();
        log.info("[玩家下线] 在线数量:{}  userId:{} -- {}"
                , userSessions.countOnline()
                , userId, userSession.getUserChannelId());
    }
}
