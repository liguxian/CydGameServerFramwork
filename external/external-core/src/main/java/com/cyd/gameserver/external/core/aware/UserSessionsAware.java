package com.cyd.gameserver.external.core.aware;

import com.cyd.gameserver.external.core.micro.session.UserSessions;

public interface UserSessionsAware {

    void setUserSessions(UserSessions<?,?> userSessions);
}
