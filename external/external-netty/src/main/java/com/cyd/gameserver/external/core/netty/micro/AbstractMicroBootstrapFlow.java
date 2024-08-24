package com.cyd.gameserver.external.core.netty.micro;

import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.aware.ExternalCoreSettingAware;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import com.cyd.gameserver.external.core.kit.hook.IdleProcessSetting;
import com.cyd.gameserver.external.core.micro.MicroBootstrapFlow;
import com.cyd.gameserver.external.core.micro.PipelineContext;
import com.cyd.gameserver.external.core.netty.DefaultExternalCoreSetting;
import com.cyd.gameserver.external.core.netty.SettingOption;
import com.cyd.gameserver.external.core.netty.handler.CmdCacheHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketIdleHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketRequestBrokerHandler;
import com.cyd.gameserver.external.core.netty.handler.SocketUserSessionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Objects;

public abstract class AbstractMicroBootstrapFlow implements MicroBootstrapFlow<ServerBootstrap>, ExternalCoreSettingAware {

    protected DefaultExternalCoreSetting setting;

    @Override
    public void setExternalCoreSetting(ExternalCoreSetting setting) {
        this.setting = (DefaultExternalCoreSetting) setting;
    }

    @Override
    public void channelInitializer(ServerBootstrap serverBootstrap) {
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                DefaultPipelineContext pipelineContext = new DefaultPipelineContext(socketChannel, setting);

                pipelineFlow(pipelineContext);
            }
        });
    }

    /**
     * 心跳相关
     *
     * @param pipelineContext
     */
    @Override
    public void pipelineIdle(PipelineContext pipelineContext) {
        IdleProcessSetting idleProcessSetting = setting.getIdleProcessSetting();
        if (Objects.isNull(idleProcessSetting)) {
            return;
        }
        //netty心跳检测
        pipelineContext.addLast("idleStateHandler", new IdleStateHandler(
                        idleProcessSetting.getReaderIdleTime(),
                        idleProcessSetting.getWriterIdleTime(),
                        idleProcessSetting.getAllIdleTime(),
                        idleProcessSetting.getTimeUnit()
                )
        );
        SocketIdleHandler socketIdleHandler = setting.option(SettingOption.socketIdleHandler);
        pipelineContext.addLast("idleHandler", socketIdleHandler);
    }

    /**
     * 自定义业务流
     *
     * @param pipelineContext
     */
    @Override
    public void pipelineCustom(PipelineContext pipelineContext) {
        //管理UserSession的handler
        SocketUserSessionHandler socketUserSessionHandler = setting.option(SettingOption.socketUserSessionHandler);
        pipelineContext.addLast("userSessionHandler", socketUserSessionHandler);

        //对外服缓存handler
        if (Objects.nonNull(ExternalGlobalConfig.externalCmdCache)) {
            pipelineContext.addLast("cmdCacheHandler", CmdCacheHandler.me());
        }

        //负责把游戏端的请求转发给 Broker（游戏网关）的 Handler
        SocketRequestBrokerHandler socketRequestBrokerHandler = setting.option(SettingOption.socketRequestBrokerHandler);
        pipelineContext.addLast("requestBrokerHandler", socketRequestBrokerHandler);
    }
}
