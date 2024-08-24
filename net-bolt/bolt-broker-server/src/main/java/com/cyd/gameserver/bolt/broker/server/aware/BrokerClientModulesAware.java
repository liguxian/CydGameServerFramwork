package com.cyd.gameserver.bolt.broker.server.aware;

import com.cyd.gameserver.bolt.broker.server.service.BrokerClientModules;

public interface BrokerClientModulesAware {

    void setBrokerClientModules(BrokerClientModules brokerClientModules);
}
