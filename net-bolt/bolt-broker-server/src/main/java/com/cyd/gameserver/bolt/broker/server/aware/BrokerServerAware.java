package com.cyd.gameserver.bolt.broker.server.aware;

import com.cyd.gameserver.bolt.broker.server.BrokerServer;

public interface BrokerServerAware {

    void setBrokerServer(BrokerServer brokerServer);
}
