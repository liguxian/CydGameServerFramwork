package com.cyd.gameserver.external.core.netty.micro.auto;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;

public class GroupChannelOptionForLinux implements GroupChannelOption{

    @Override
    public EventLoopGroup boosGroup() {
        return new EpollEventLoopGroup(1, EventLoopGroupThreadFactory.bossThreadFactory());
    }

    @Override
    public EventLoopGroup workerGroup() {
        int availableProcessors = Runtime.getRuntime().availableProcessors() << 1;
        return new EpollEventLoopGroup(availableProcessors, EventLoopGroupThreadFactory.bossThreadFactory());
    }

    @Override
    public Class<? extends ServerChannel> channelClass() {
        return EpollServerSocketChannel.class;
    }
}
