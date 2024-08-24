package com.cyd.gameserver.bolt.broker.server.balanced.region;

import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.bolt.broker.server.balanced.BrokerClientProxy;
import com.cyd.gameserver.bolt.core.loadbalance.ElementSelector;
import com.cyd.gameserver.bolt.core.loadbalance.RandomElementSelector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * 同一逻辑服的多个实例负载均衡
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultBrokerClientRegion implements BrokerClientRegion{
    @Getter
    final String tag;

    final Map<Integer, BrokerClientProxy> brokerClientProxyMap = new NonBlockingHashMap<>();

    ElementSelector<BrokerClientProxy> elementSelector;

    public DefaultBrokerClientRegion(String tag) {
        this.tag = tag;
    }

    @Override
    public int count() {
        return brokerClientProxyMap.size();
    }

    @Override
    public void add(BrokerClientProxy brokerClientProxy) {
        brokerClientProxyMap.put(brokerClientProxy.getIdHash(), brokerClientProxy);
        resetSelector();
    }

    @Override
    public BrokerClientProxy getBrokerClientProxy(HeadMetadata headMetadata) {
        int endPointClientId = headMetadata.getEndPointClientId();

        if(endPointClientId != 0 && brokerClientProxyMap.containsKey(endPointClientId)) {
            BrokerClientProxy brokerClientProxy = brokerClientProxyMap.get(endPointClientId);
            //如果找到了就返回，没找到就随机找一个
            if(Objects.nonNull(brokerClientProxy)) {
                return brokerClientProxy;
            }
        }

        if(Objects.isNull(elementSelector)) {
            return null;
        }
        return elementSelector.get();
    }

    @Override
    public Map<Integer, BrokerClientProxy> getBrokerClientProxyMap() {
        return brokerClientProxyMap;
    }

    @Override
    public void remove(int id) {
        brokerClientProxyMap.remove(id);
        resetSelector();
    }

    private void resetSelector() {
        //随机选择器
        elementSelector = new RandomElementSelector<>(new ArrayList<>(brokerClientProxyMap.values()));
    }
}
