package com.cyd.gameserver.bolt.broker.server.balanced;

/**
 * 逻辑服管理器
 */
public interface BrokerClientLoadBalanced {

    /**
     * 注册逻辑服代理
     * @param brokerClientProxy
     */
    void register(BrokerClientProxy brokerClientProxy);

    /**
     * 移除逻辑服代理
     * @param brokerClientProxy
     */
    void remove(BrokerClientProxy brokerClientProxy);
}
