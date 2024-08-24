package com.cyd.gameserver.external.core.netty.handler;

import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.aware.ExternalCoreSettingAware;
import com.cyd.gameserver.external.core.kit.hook.IdleHook;
import com.cyd.gameserver.external.core.kit.hook.IdleProcessSetting;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import com.cyd.gameserver.external.core.message.ExternalMessageCmdCode;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import com.cyd.gameserver.external.core.netty.DefaultExternalCoreSetting;
import com.cyd.gameserver.external.core.netty.micro.session.SocketUserSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.Objects;

@ChannelHandler.Sharable
public class SocketIdleHandler extends ChannelInboundHandlerAdapter implements ExternalCoreSettingAware {

    IdleHook<IdleStateEvent> idleHook;

    /**
     * 是否响应给客户端
     */
    boolean pong;

    UserSessions<ChannelHandlerContext, SocketUserSession> userSessions;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ExternalMessage externalMessage = (ExternalMessage) msg;
        if (ExternalMessageCmdCode.idle == externalMessage.getCmdCode()) {
            if (pong) {
                ctx.writeAndFlush(externalMessage);
            }

            return;
        }
        //如果不是心跳消息，交给下一个handler执行
        ctx.fireChannelRead(msg);
    }

    /**
     * 心跳事件：长时间无消息时的触发事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            boolean close = true;
            SocketUserSession userSession = userSessions.getUserSession(ctx);

            if (Objects.nonNull(idleHook)) {
                close = idleHook.callback(userSession, event);
            }

            if (close) {
                userSessions.remove(userSession);
            }

            return;
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void setExternalCoreSetting(ExternalCoreSetting coreSetting) {
        if (Objects.nonNull(userSessions)) {
            return;
        }

        DefaultExternalCoreSetting setting = (DefaultExternalCoreSetting) coreSetting;
        IdleProcessSetting idleProcessSetting = setting.getIdleProcessSetting();
        if (Objects.nonNull(idleProcessSetting)) {
            idleHook = (IdleHook<IdleStateEvent>) idleProcessSetting.getIdleHook();
        }
        pong = idleProcessSetting.isPong();
        userSessions = (UserSessions<ChannelHandlerContext, SocketUserSession>) setting.getUserSessions();
    }
}
