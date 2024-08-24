package com.cyd.gameserver.external.core.netty.handler;

import com.cyd.gameserver.action.skeleton.core.codec.ProtoDataCodec;
import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.external.core.aware.UserSessionsAware;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import com.cyd.gameserver.external.core.netty.Test1;
import com.cyd.gameserver.external.core.netty.micro.session.SocketUserSession;
import com.cyd.gameserver.external.core.netty.micro.session.SocketUserSessions;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@ChannelHandler.Sharable
@Slf4j(topic = LogName.ExternalTopic)
public class SocketUserSessionHandler extends ChannelInboundHandlerAdapter implements UserSessionsAware {

    /**
     * session管理器
     */
    SocketUserSessions userSessions;

    @Override
    public void setUserSessions(UserSessions<?, ?> userSessions) {
        this.userSessions = (SocketUserSessions) userSessions;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        //连接时添加到session管理器
        userSessions.add(ctx);
        while(true) {
            Random random = new Random();
            int i = random.nextInt(15);
            ExternalMessage externalMessage = new ExternalMessage();
            externalMessage.setCmdCode(1);
            externalMessage.setCmdMerge(65537);
            externalMessage.setData(ProtoDataCodec.encode(new Test1().setMsg("cyd666" + i)));
            externalMessage.setMsgId(2);
            ctx.writeAndFlush(externalMessage);
            Thread.sleep(1000);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //从session管理器中移除
        SocketUserSession userSession = userSessions.getUserSession(ctx);
        userSessions.remove(userSession);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        // 从 session 管理中移除
        var userSession = this.userSessions.getUserSession(ctx);
        this.userSessions.remove(userSession);
    }
}
