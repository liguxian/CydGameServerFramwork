package com.cyd.gameserver.bolt.broker.server.balanced.region;

public interface BrokerClientRegionFactory {

    BrokerClientRegion create(String tag);
}
