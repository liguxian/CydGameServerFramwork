package com.cyd.gameserver.external.core.netty.micro.session;

import com.cyd.gameserver.external.core.micro.session.UserChannelId;
import com.cyd.gameserver.external.core.micro.session.UserSessionOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * 长连接用户session
 */
 public final class SocketUserSession extends AbstractUserSession {

    public SocketUserSession(Channel channel) {
        this.channel = channel;
        String channelId = channel.id().asLongText();
        this.userChannelId = new UserChannelId(channelId);
    }

    @Override
    public ChannelFuture writeAndFlush(Object message) {
        return channel.writeAndFlush(message);
    }

    @Override
    public String getIp() {
        String realIp = option(UserSessionOption.realIp);
        if (Objects.isNull(realIp)) {
            InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
            return socketAddress.getHostString();
        }
        return realIp;
    }

    @Override
    public boolean isActive() {
        return Objects.nonNull(channel) && channel.isActive();
    }
}
