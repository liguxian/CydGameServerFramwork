package com.cyd.gameserver.test;

import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.BrokerServerBuilder;

/**
 * Hello world!
 *
 */
public class BrokerApplication
{
    public static void main( String[] args )
    {
        BrokerServerBuilder brokerServerBuilder = BrokerServer.newBuilder();
        BrokerServer brokerServer = brokerServerBuilder.build();

        brokerServer.startup();
    }
}
