package com.cyd.gameserver.external.core.netty.micro.auto;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;

public class GroupChannelOptionForMac implements GroupChannelOption {

    @Override
    public EventLoopGroup boosGroup() {
        return new KQueueEventLoopGroup(1, EventLoopGroupThreadFactory.bossThreadFactory());
    }

    @Override
    public EventLoopGroup workerGroup() {
        int availableProcessors = Runtime.getRuntime().availableProcessors() << 1;
        return new KQueueEventLoopGroup(availableProcessors, EventLoopGroupThreadFactory.bossThreadFactory());
    }

    @Override
    public Class<? extends ServerChannel> channelClass() {
        return KQueueServerSocketChannel.class;
    }
}
