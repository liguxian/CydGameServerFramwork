package com.cyd.gameserver.external.core.netty.micro.session;

import com.cyd.gameserver.common.kit.attr.AttrOptions;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.kit.hook.UserHook;
import com.cyd.gameserver.external.core.micro.session.UserChannelId;
import com.cyd.gameserver.external.core.micro.session.UserSession;
import com.cyd.gameserver.external.core.micro.session.UserSessionOption;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import io.netty.channel.ChannelFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashMapLong;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PROTECTED)
abstract class AbstractUserSessions<ChannelHandlerContext, Session extends UserSession> implements UserSessions<ChannelHandlerContext, Session> {

    /**
     * 动态属性
     */
    @Getter
    final AttrOptions options = new AttrOptions();

    final NonBlockingHashMapLong<Session> userIdMap = new NonBlockingHashMapLong<>();

    final Map<UserChannelId, Session> userChannelIdMap = new NonBlockingHashMap<>();

    @Setter
    UserHook userHook;

    @Override
    public Session getUserSession(UserChannelId userChannelId) {
        return userChannelIdMap.get(userChannelId);
    }

    @Override
    public Session getUserSession(long userId) {
        return userIdMap.get(userId);
    }

    @Override
    public boolean exist(long userId) {
        return userIdMap.containsKey(userId);
    }

    @Override
    public void remove(long userId, Object msg) {
        ifPresent(userId, userSession -> {
            ChannelFuture channelFuture = userSession.writeAndFlush(msg);
            channelFuture.addListener(future -> {
                remove(userSession);
            });
        });
    }

    @Override
    public void foreach(Consumer<Session> consumer) {
        userChannelIdMap.values().forEach(consumer);
    }

    void userHookInto(UserSession userSession) {
        if(Objects.isNull(userHook)) {
            return;
        }
        this.userHook.into(userSession);
    }

    void userHookQuit(UserSession userSession) {
        if(Objects.isNull(userHook)) {
            return;
        }
        this.userHook.quit(userSession);
    }

    void setDefault(UserSession userSession) {
        ExternalJoinEnum joinEnum = this.option(UserSessionOption.externalJoin);
        userSession.option(UserSessionOption.externalJoin, joinEnum);
    }
}
