package com.cyd.gameserver.bolt.broker.server.balanced;

import com.alipay.remoting.rpc.RpcServer;
import com.cyd.gameserver.bolt.broker.server.BrokerServer;
import com.cyd.gameserver.bolt.broker.server.balanced.region.BrokerClientRegion;
import com.cyd.gameserver.bolt.core.client.BrokerClientType;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.jctools.maps.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *对外服和逻辑服的负载管理器
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BalancedManager {

    /**
     * 逻辑服负载均衡
     */
    @Getter
    final LogicBrokerClientLoadBalanced logicBalanced = new LogicBrokerClientLoadBalanced();

    /**
     * 对外服负载均衡
     */
    @Getter
    final ExternalBrokerClientLoadBalanced externalBalanced = new ExternalBrokerClientLoadBalanced();

    /** key:address value:proxy */
    final Map<String, BrokerClientProxy> refMap = new NonBlockingHashMap<>();

    final BrokerServer brokerServer;

    public BalancedManager(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
    }

    public BrokerClientLoadBalanced getLoadBalanced(BrokerClientType brokerClientType) {
        if(BrokerClientType.LOGIC.equals(brokerClientType)) {
            return logicBalanced;
        }

        return externalBalanced;
    }

    public void register(BrokerClientModuleMessage brokerClientModuleMessage) {
        BrokerClientType brokerClientType = brokerClientModuleMessage.getBrokerClientType();
        BrokerClientLoadBalanced loadBalanced = this.getLoadBalanced(brokerClientType);

        RpcServer rpcServer = brokerServer.getRpcServer();
        BrokerClientProxy brokerClientProxy = new BrokerClientProxy(brokerClientModuleMessage, rpcServer);
//        brokerClientProxy.setCmdMergeList(null);
        loadBalanced.register(brokerClientProxy);

        refMap.put(brokerClientModuleMessage.getAddress(), brokerClientProxy);
    }

    public BrokerClientProxy remove(String address){
        BrokerClientProxy brokerClientProxy = refMap.get(address);

        BrokerClientLoadBalanced loadBalanced = this.getLoadBalanced(brokerClientProxy.getBrokerClientType());
        loadBalanced.remove(brokerClientProxy);

//        refMap.remove(address);
        return brokerClientProxy;
    }

    public List<BrokerClientProxy> listBrokerClientProxy(){
        List<BrokerClientProxy> list = new ArrayList<>();
        //对外服列表
        List<BrokerClientProxy> externalList = externalBalanced.list();
        list.addAll(externalList);

        //逻辑服列表
        val brokerClientRegions = logicBalanced.listBrokerClientRegion();
        for (BrokerClientRegion brokerClientRegion : brokerClientRegions) {
            val brokerClientProxies = brokerClientRegion.listBrokerClientProxy();
            list.addAll(brokerClientProxies);
        }

        return list;
    }

}
