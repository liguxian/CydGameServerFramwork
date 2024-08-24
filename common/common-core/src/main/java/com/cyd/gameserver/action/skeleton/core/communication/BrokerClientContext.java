package com.cyd.gameserver.action.skeleton.core.communication;

import com.cyd.gameserver.action.skeleton.protocol.processor.SimpleServerInfo;

public interface BrokerClientContext extends ChannelContext, SimpleServer {

    /**
     * 发送消息到游戏网关
     *
     * @param request 消息
     * @throws Exception e
     */
    void oneway(final Object request) throws Exception;

    /**
     * 框架网络通讯聚合接口(轮询获取一个与broker通讯的客户端)
     *
     * @return 框架网络通信聚合接口
     */
    CommunicationAggregationContext getCommunicationAggregationContext();

    /**
     * 推送通讯相关 - 得到广播通讯上下文
     *
     * @return 广播通讯上下文
     */
    default BroadcastContext getBroadcastContext() {
        return this.getCommunicationAggregationContext();
    }

    /**
     * 推送通讯相关 - 得到顺序的 - 广播通讯上下文
     *
     * @return 顺序的 - 广播通讯上下文
     */
    default BroadcastOrderContext getBroadcastOrderContext() {
        return this.getCommunicationAggregationContext();
    }

    /**
     * 得到 processor 上下文
     *
     * @return processor 上下文
     */
    default ProcessorContext getProcessorContext() {
        return this.getCommunicationAggregationContext();
    }
}
