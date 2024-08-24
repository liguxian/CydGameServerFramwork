package com.cyd.gameserver.external.core.netty.handler;

import com.cyd.gameserver.external.core.cacahe.ExternalCmdCache;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * 对外服缓存handler
 */
@ChannelHandler.Sharable
public class CmdCacheHandler extends SimpleChannelInboundHandler<ExternalMessage> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //若没有开启缓存，则移除此handler
        ExternalCmdCache externalCmdCache = ExternalGlobalConfig.externalCmdCache;
        if (Objects.isNull(externalCmdCache)) {
            ctx.pipeline().remove(this);
        }

        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ExternalMessage externalMessage) {
        ExternalCmdCache externalCmdCache = ExternalGlobalConfig.externalCmdCache;
        ExternalMessage cache = externalCmdCache.getCache(externalMessage);
        if (Objects.nonNull(cache)) {
            ctx.writeAndFlush(cache);
            return;
        }

        ctx.fireChannelRead(externalCmdCache);
    }

    private CmdCacheHandler() {
    }

    public static CmdCacheHandler me() {
        return Holder.ME;
    }

    private static class Holder {
        static final CmdCacheHandler ME = new CmdCacheHandler();
    }
}
