package com.cyd.gameserver.bolt.broker.server.balanced.region;

import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.bolt.broker.server.balanced.BrokerClientProxy;

import java.util.Collection;
import java.util.Map;

/**
 * 同一逻辑服的多个实例管理器
 */
public interface BrokerClientRegion {

    /**
     * 获取逻辑服tag
     */
    String getTag();

    /**
     * 当前逻辑服有多少个实例
     */
    int count();

    /**
     * 添加实例
     */
    void add(BrokerClientProxy brokerClientProxy);

    /**
     * 根据请求元信息获取一个实例
     */
    BrokerClientProxy getBrokerClientProxy(HeadMetadata headMetadata);

    /**
     * 获取实例map
     */
    Map<Integer, BrokerClientProxy> getBrokerClientProxyMap();

    /**
     * 获取实例list
     */
    default Collection<BrokerClientProxy> listBrokerClientProxy() {
        return getBrokerClientProxyMap().values();
    }

    /**
     * 根据逻辑服id移除逻辑服
     */
    void remove(int id);
}
