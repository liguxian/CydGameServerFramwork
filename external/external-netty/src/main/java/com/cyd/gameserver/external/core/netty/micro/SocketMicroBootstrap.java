package com.cyd.gameserver.external.core.netty.micro;

import com.cyd.gameserver.action.skeleton.toy.IoGameBanner;
import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.common.kit.system.OsInfo;
import com.cyd.gameserver.external.core.micro.MicroBootstrapFlow;
import com.cyd.gameserver.external.core.netty.micro.auto.GroupChannelOptionForLinux;
import com.cyd.gameserver.external.core.netty.micro.auto.GroupChannelOptionForOther;
import com.cyd.gameserver.external.core.netty.micro.auto.GroupChannelOption;
import com.cyd.gameserver.external.core.netty.micro.auto.GroupChannelOptionForMac;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * netty启动器
 */
@Slf4j(topic = LogName.ExternalTopic)
public class SocketMicroBootstrap extends AbstractMicroBootstrap {
    @Override
    public void startUp() {
        GroupChannelOption groupChannelOption = createGroupChannelOption();
        EventLoopGroup boosGroup = groupChannelOption.boosGroup();
        EventLoopGroup workerGroup = groupChannelOption.workerGroup();
        Class<? extends ServerChannel> channelClass = groupChannelOption.channelClass();

        ServerBootstrap bootstrap = new ServerBootstrap().group(boosGroup, workerGroup).channel(channelClass);

        //设置启动器的业务流程handler
        MicroBootstrapFlow<ServerBootstrap> microBootstrapFlow = setting.getMicroBootstrapFlow();
        microBootstrapFlow.createFlow(bootstrap);

        //绑定玩家连接的端口
        final int externalPort = setting.getPort();
        ChannelFuture channelFuture = bootstrap.bind(externalPort);

        try {
            IoGameBanner.render();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private GroupChannelOption createGroupChannelOption() {
        //判断操作系统
        OsInfo osInfo = OsInfo.me();

        if (osInfo.isLinux()) {
            return new GroupChannelOptionForLinux();
        } else if (osInfo.isMac()) {
            return new GroupChannelOptionForMac();
        } else {
            return new GroupChannelOptionForOther();
        }
    }
}
