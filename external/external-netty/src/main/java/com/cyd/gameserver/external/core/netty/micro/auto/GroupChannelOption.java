package com.cyd.gameserver.external.core.netty.micro.auto;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public interface GroupChannelOption {

    //boss事件循环组  循环处理连接请求
    EventLoopGroup boosGroup();

    //worker事件循环组  循环处理业务请求
    EventLoopGroup workerGroup();

    Class<? extends ServerChannel> channelClass();
}
