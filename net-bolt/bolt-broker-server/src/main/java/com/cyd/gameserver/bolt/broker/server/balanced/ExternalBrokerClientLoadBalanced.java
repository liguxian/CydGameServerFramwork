package com.cyd.gameserver.bolt.broker.server.balanced;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 对外服管理器
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalBrokerClientLoadBalanced implements BrokerClientLoadBalanced {

    /**
     * 对外服的关联 map
     * <pre>
     *     key : external id
     *     value : external info
     * </pre>
     */
    final Map<Integer, BrokerClientProxy> map = new NonBlockingHashMap<>();
    /** 对外服 list */
    List<BrokerClientProxy> list = Collections.emptyList();

    @Override
    public void register(BrokerClientProxy brokerClientProxy) {
        map.put(brokerClientProxy.getIdHash(), brokerClientProxy);
        resetSelector();
    }

    @Override
    public void remove(BrokerClientProxy brokerClientProxy) {
        map.remove(brokerClientProxy.getIdHash());
        resetSelector();
    }

    private void resetSelector() {
        this.list = new CopyOnWriteArrayList<>(map.values());
    }

    public List<BrokerClientProxy> list(){
        return this.list;
    }

    public BrokerClientProxy get(int externalId){
        return map.get(externalId);
    }

    public int count(){
        return this.map.size();
    }

    public boolean contains(int externalId){
        return map.containsKey(externalId);
    }
}
