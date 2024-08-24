package com.cyd.gameserver.external.core.netty.micro.session;

import com.cyd.gameserver.external.core.micro.session.UserChannelId;
import com.cyd.gameserver.external.core.micro.session.UserSessionState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * tcp、websocket 使用的 session 管理器
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocketUserSessions extends AbstractUserSessions<ChannelHandlerContext, SocketUserSession>{
    /** 用户 session，与channel是 1:1 的关系 */
    static final AttributeKey<SocketUserSession> userSessionKey = AttributeKey.valueOf("userSession");

    final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public SocketUserSession add(ChannelHandlerContext channelHandlerContext) {
        Channel channel = channelHandlerContext.channel();
        SocketUserSession socketUserSession = new SocketUserSession(channel);
        UserChannelId userChannelId = socketUserSession.getUserChannelId();
        userChannelIdMap.put(userChannelId, socketUserSession);
        channelGroup.add(channel);

        //channel中也保存session信息
        channel.attr(SocketUserSessions.userSessionKey).set(socketUserSession);

        setDefault(socketUserSession);
        return socketUserSession;
    }

    @Override
    public SocketUserSession getUserSession(ChannelHandlerContext channelHandlerContext) {
        Channel channel = channelHandlerContext.channel();
        return channel.attr(SocketUserSessions.userSessionKey).get();
    }

    @Override
    public boolean setUserId(UserChannelId userChannelId, long userId) {
        SocketUserSession socketUserSession = getUserSession(userChannelId);
        if(Objects.isNull(socketUserSession)) {
            return false;
        }

        if(!socketUserSession.isActive()) {
            remove(socketUserSession);
            return false;
        }

        socketUserSession.setUserId(userId);
        userIdMap.put(userId, socketUserSession);

        //上线通知（触发上线钩子）
        if(socketUserSession.isVerifyIdentity()) {
            this.userHookInto(socketUserSession);
        }

        return true;
    }

    @Override
    public void remove(SocketUserSession session) {
        if(Objects.isNull(session)) {
            return;
        }

        if(session.state == UserSessionState.DEAD) {
            return;
        }

        UserChannelId userChannelId = session.getUserChannelId();
        long userId = session.getUserId();
        Channel channel = session.channel;
        userChannelIdMap.remove(userChannelId);
        userIdMap.remove(userId);
        channelGroup.remove(channel);

        if(session.isVerifyIdentity()) {
            session.setState(UserSessionState.DEAD);
            //触发下线钩子
            this.userHookQuit(session);
        }

        //关闭用户的连接
        channel.close();
    }

    @Override
    public int countOnline() {
        return channelGroup.size();
    }

    @Override
    public void broadCast(Object msg) {
        channelGroup.writeAndFlush(msg);
    }
}
