package com.cyd.gameserver.bolt.core.aware;

import com.cyd.gameserver.bolt.core.client.BrokerClient;

public interface BrokerClientAware {

    void setBrokerClient(BrokerClient brokerClient);
}
