package com.cyd.gameserver.bolt.broker.server.service;

import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Collection;
import java.util.Map;

/**
 * 网关客户端模块信息管理器
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class DefaultBrokerClientModules implements BrokerClientModules {

    Map<String, BrokerClientModuleMessage> map = new NonBlockingHashMap<>();

    @Override
    public void add(BrokerClientModuleMessage moduleMessage) {
        map.put(moduleMessage.getId(), moduleMessage);
    }

    @Override
    public BrokerClientModuleMessage removeById(String id) {
        BrokerClientModuleMessage brokerClientModuleMessage = map.get(id);
        map.remove(id);
        return brokerClientModuleMessage;
    }

    @Override
    public Collection<BrokerClientModuleMessage> list() {
        return map.values();
    }
}
