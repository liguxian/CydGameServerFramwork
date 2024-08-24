package com.cyd.gameserver.external.core.netty.micro;

import com.cyd.gameserver.external.core.micro.PipelineContext;
import com.cyd.gameserver.external.core.netty.DefaultExternalCoreSetting;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

public record DefaultPipelineContext(Channel channel, DefaultExternalCoreSetting setting) implements PipelineContext {

    @Override
    public void addFirst(String name, Object handler) {

        //把setting中的属性赋值到handler中
        setting.aware(handler);

        if(handler instanceof ChannelHandler channelHandler) {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addFirst(name, channelHandler);
        }
    }

    @Override
    public void addLast(String name, Object handler) {

        //把setting中的属性赋值到handler中
        setting.aware(handler);

        if(handler instanceof ChannelHandler channelHandler) {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(name, channelHandler);
        }
    }

    @Override
    public void remove(String name) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.remove(name);
    }
}
