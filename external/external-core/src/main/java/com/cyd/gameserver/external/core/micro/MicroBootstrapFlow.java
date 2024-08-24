package com.cyd.gameserver.external.core.micro;

/**
 * netty启动器业务流 handler流
 */
public interface MicroBootstrapFlow<Bootstrap> {

    default void createFlow(Bootstrap bootstrap) {
        option(bootstrap);
        channelInitializer(bootstrap);
    }

    //给服务器做一些option设置
    void option(Bootstrap bootstrap);

    //给netty服务器做一些业务编排
    void channelInitializer(Bootstrap bootstrap);

    //新建连接时的执行流程
    default void pipelineFlow(PipelineContext pipelineContext) {
        pipelineCodec(pipelineContext);
        pipelineIdle(pipelineContext);
        pipelineCustom(pipelineContext);
    }

    //编解码
    void pipelineCodec(PipelineContext pipelineContext);

    //心跳相关
    void pipelineIdle(PipelineContext pipelineContext);

    //自定义业务流
    void pipelineCustom(PipelineContext pipelineContext);
}
