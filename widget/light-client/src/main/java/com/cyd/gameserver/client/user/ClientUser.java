package com.cyd.gameserver.client.user;

import com.cyd.gameserver.common.kit.attr.AttrOptionDynamic;

/**
 *客户信息
 */
public interface ClientUser extends AttrOptionDynamic {

    ClientUserChannel getClientUserChannel();

    long getUserId();

    void setUserId(long userId);

    String getNickname();

    void setNickname(String nickname);
}
