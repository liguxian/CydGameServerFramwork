package com.cyd.gameserver.client.join.handler;

import com.cyd.gameserver.client.user.ClientUser;
import com.cyd.gameserver.client.user.ClientUserChannel;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ClientMessageHandler extends SimpleChannelInboundHandler<ExternalMessage> {

    final ClientUserChannel clientUserChannel;

    public ClientMessageHandler(ClientUser clientUser) {
        this.clientUserChannel = clientUser.getClientUserChannel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ExternalMessage externalMessage) {
        clientUserChannel.read(externalMessage);
    }
}
