package com.cyd.gameserver.bolt.broker.server.service;

import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;

import java.util.Collection;

/**
 * 网关客户端模块信息管理器
 */
public interface BrokerClientModules {

    void add(BrokerClientModuleMessage moduleMessage);

    BrokerClientModuleMessage removeById(String id);

    Collection<BrokerClientModuleMessage> list();
}
