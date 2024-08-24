package com.cyd.gameserver.external.core.netty.micro.auto;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class GroupChannelOptionForOther implements GroupChannelOption {

    @Override
    public EventLoopGroup boosGroup() {
        return new NioEventLoopGroup(1, EventLoopGroupThreadFactory.bossThreadFactory());
    }

    @Override
    public EventLoopGroup workerGroup() {
        int availableProcessors = Runtime.getRuntime().availableProcessors() << 1;
        return new NioEventLoopGroup(availableProcessors, EventLoopGroupThreadFactory.bossThreadFactory());
    }

    @Override
    public Class<? extends ServerChannel> channelClass() {
        return NioServerSocketChannel.class;
    }
}
