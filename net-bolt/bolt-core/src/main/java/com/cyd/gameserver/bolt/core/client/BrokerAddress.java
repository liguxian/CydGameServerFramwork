package com.cyd.gameserver.bolt.core.client;

public record BrokerAddress(String ip, int port) {

    public String getAddress() {
        return ip + ":" + port;
    }
}
