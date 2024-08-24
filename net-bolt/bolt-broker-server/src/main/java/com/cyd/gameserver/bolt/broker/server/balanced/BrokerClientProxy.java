package com.cyd.gameserver.bolt.broker.server.balanced;

import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcServer;
import com.cyd.gameserver.bolt.core.client.BrokerClientType;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * 网关服里的逻辑服客户端代理
 */
@Getter
@Setter
public class BrokerClientProxy {

    final String id;
    final int idHash;
    final String name;
    final String tag;
    final String address;
    final BrokerClientType brokerClientType;
    final RpcServer rpcServer;
    int timeMillis = GlobalConfig.timeoutMillis;
    int status;
    List<Integer> cmdMergeList;

    public BrokerClientProxy(BrokerClientModuleMessage brokerClientModuleMessage, RpcServer rpcServer) {
        this.id = brokerClientModuleMessage.getId();
        this.idHash = brokerClientModuleMessage.getIdHash();
        this.name = brokerClientModuleMessage.getName();
        this.tag = brokerClientModuleMessage.getTag();
        this.address = brokerClientModuleMessage.getAddress();
        this.brokerClientType = brokerClientModuleMessage.getBrokerClientType();
        this.rpcServer = rpcServer;
        this.cmdMergeList = brokerClientModuleMessage.getCmdMergeList();
        this.status = brokerClientModuleMessage.getStatus();
    }

    public void oneway(Object request) throws RemotingException, InterruptedException {
        rpcServer.oneway(address, request);
    }

    public <T> T invokeSync(Object message) throws RemotingException, InterruptedException {
        return (T) rpcServer.invokeSync(address, message, timeMillis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BrokerClientProxy that)) {
            return false;
        }

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
