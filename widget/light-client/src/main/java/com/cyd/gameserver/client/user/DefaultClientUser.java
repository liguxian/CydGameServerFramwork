package com.cyd.gameserver.client.user;

import com.cyd.gameserver.common.kit.attr.AttrOptions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class DefaultClientUser implements ClientUser{

    final AttrOptions options = new AttrOptions();

    /**
     * userChannel用于读写
     */
    final ClientUserChannel clientUserChannel = new ClientUserChannel(this);

    /** true 已经登录成功 */
    boolean loginSuccess;

    long userId;
    /** 昵称 */
    String nickname;
}
