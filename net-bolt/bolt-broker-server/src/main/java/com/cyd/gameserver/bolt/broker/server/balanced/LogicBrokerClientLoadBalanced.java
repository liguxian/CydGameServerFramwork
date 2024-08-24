package com.cyd.gameserver.bolt.broker.server.balanced;

import com.cyd.gameserver.bolt.broker.server.balanced.region.BrokerClientRegion;
import com.cyd.gameserver.bolt.broker.server.balanced.region.BrokerClientRegionFactory;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 逻辑服管理器
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogicBrokerClientLoadBalanced implements BrokerClientLoadBalanced {

    /**
     * 逻辑服tag 与逻辑服域的关联
     * <pre>
     *     key : tag
     *     value : BrokerClientRegion
     * </pre>
     */
    final Map<String, BrokerClientRegion> tagClientRegionMap = new NonBlockingHashMap<>();

    /**
     * 路由与逻辑服域的关联
     * <pre>
     *     key : cmdMerge
     *     value : BrokerClientRegion
     * </pre>
     */
    final Map<Integer, BrokerClientRegion> cmdClientRegionMap = new NonBlockingHashMap<>();

    /**
     * 逻辑服idHash 与逻辑服域的关联
     * <pre>
     *     key : idHash
     *     value : BrokerClientProxy
     * </pre>
     */
    final Map<Integer, BrokerClientProxy> serverIdClientProxyMap = new NonBlockingHashMap<>();

    @Setter
    BrokerClientRegionFactory brokerClientRegionFactory;

    @Override
    public void register(BrokerClientProxy brokerClientProxy) {
        String tag = brokerClientProxy.getTag();
        BrokerClientRegion brokerClientRegion = getBrokerClientRegionByTag(tag);
        brokerClientRegion.add(brokerClientProxy);

        val cmdMergeList = brokerClientProxy.getCmdMergeList();
        for (Integer cmdMerge : cmdMergeList) {
            cmdClientRegionMap.put(cmdMerge, brokerClientRegion);
        }

        serverIdClientProxyMap.put(brokerClientProxy.getIdHash(), brokerClientProxy);
    }

    @Override
    public void remove(BrokerClientProxy brokerClientProxy) {
        int id = brokerClientProxy.getIdHash();
        // 相同业务模块（逻辑服）的信息域
        String tag = brokerClientProxy.getTag();
        BrokerClientRegion brokerClientRegion = getBrokerClientRegionByTag(tag);
        brokerClientRegion.remove(id);

        this.serverIdClientProxyMap.remove(id);
    }

    private BrokerClientRegion getBrokerClientRegionByTag(String tag) {
        BrokerClientRegion brokerClientRegion = tagClientRegionMap.get(tag);

        //无锁化
        if(Objects.isNull(brokerClientRegion)) {
            brokerClientRegion = brokerClientRegionFactory.create(tag);
            brokerClientRegion = tagClientRegionMap.putIfAbsent(tag, brokerClientRegion);
            if(Objects.isNull(brokerClientRegion)) {
                brokerClientRegion = tagClientRegionMap.get(tag);
            }
        }

        return brokerClientRegion;
    }

    public BrokerClientRegion getBrokerClientRegion(int cmdMerge) {
        // 通过 路由信息 得到对应的逻辑服列表（域）
        BrokerClientRegion region = this.cmdClientRegionMap.get(cmdMerge);

        if (Objects.isNull(region)) {
            return null;
        }

        return region;
    }

    public Collection<BrokerClientRegion> listBrokerClientRegion() {
        return this.tagClientRegionMap.values();
    }

    public BrokerClientProxy getBrokerClientProxyByIdHash(int idHash) {
        return this.serverIdClientProxyMap.get(idHash);
    }
}
