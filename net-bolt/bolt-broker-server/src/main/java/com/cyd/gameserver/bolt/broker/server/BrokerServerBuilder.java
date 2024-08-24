package com.cyd.gameserver.bolt.broker.server;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.RpcServer;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerClientModulesAware;
import com.cyd.gameserver.bolt.broker.server.aware.BrokerServerAware;
import com.cyd.gameserver.bolt.broker.server.balanced.BalancedManager;
import com.cyd.gameserver.bolt.broker.server.balanced.LogicBrokerClientLoadBalanced;
import com.cyd.gameserver.bolt.broker.server.balanced.region.BrokerClientRegionFactory;
import com.cyd.gameserver.bolt.broker.server.balanced.region.DefaultBrokerClientRegion;
import com.cyd.gameserver.bolt.broker.server.processor.RegisterBrokerClientModuleMessageBrokerProcessor;
import com.cyd.gameserver.bolt.broker.server.processor.RequestMessageBrokerProcessor;
import com.cyd.gameserver.bolt.broker.server.processor.ResponseMessageBrokerProcessor;
import com.cyd.gameserver.bolt.broker.server.processor.connection.CloseConnectionEventBrokerProcessor;
import com.cyd.gameserver.bolt.broker.server.processor.connection.ConnectionEventBrokerProcessor;
import com.cyd.gameserver.bolt.broker.server.service.BrokerClientModules;
import com.cyd.gameserver.bolt.broker.server.service.DefaultBrokerClientModules;
import com.cyd.gameserver.bolt.core.aware.AwareInject;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import lombok.Setter;
import org.jctools.maps.NonBlockingHashMap;

import java.util.*;
import java.util.function.Supplier;

public class BrokerServerBuilder implements AwareInject {

    final BrokerServer brokerServer = new BrokerServer();

    @Setter
    String brokerId;

    @Setter
    int port = GlobalConfig.brokerPort;

    final Map<ConnectionEventType, Supplier<ConnectionEventProcessor>> connectionEventTypeProcessorMap = new NonBlockingHashMap<>();

    final List<Supplier<UserProcessor<?>>> userProcessors = new ArrayList<>();

    final BrokerClientModules brokerClientModules = new DefaultBrokerClientModules();

    /**
     * 网关启动模式，默认单机
     */
    @Setter
    BrokerRunModeEnum brokerRunMode = BrokerRunModeEnum.STANDALONE;

    @Setter
    BrokerClientRegionFactory brokerClientRegionFactory = DefaultBrokerClientRegion::new;

    BrokerServerBuilder() {
        defaultSetting();
    }

    public BrokerServer build() {
        checked();

        if (Objects.isNull(brokerId)) {
            brokerId = UUID.randomUUID().toString();
        }

        BalancedManager balancedManager = brokerServer.getBalancedManager();
        LogicBrokerClientLoadBalanced logicBalanced = balancedManager.getLogicBalanced();
        logicBalanced.setBrokerClientRegionFactory(brokerClientRegionFactory);

        brokerServer
                .setBrokerId(brokerId)
                .setPort(port)
                .setBrokerRunMode(brokerRunMode)
                .setBrokerClientModules(brokerClientModules);

        brokerServer.initRpcServer();
        RpcServer rpcServer = brokerServer.getRpcServer();

        connectionEventTypeProcessorMap.forEach(((connectionEventType, supplier) -> {
            ConnectionEventProcessor connectionEventProcessor = supplier.get();
            aware(connectionEventProcessor);
            rpcServer.addConnectionEventProcessor(connectionEventType, connectionEventProcessor);
        }));
        userProcessors.forEach(supplier -> {
            UserProcessor<?> userProcessor = supplier.get();
            aware(userProcessor);
            rpcServer.registerUserProcessor(userProcessor);
        });

        return brokerServer;
    }

    private void checked() {
        if (this.port <= 0) {
            throw new RuntimeException("port error!");
        }

        if (Objects.isNull(this.brokerRunMode)) {
            throw new RuntimeException("brokerRunMode expected: " + Arrays.toString(BrokerRunModeEnum.values()));
        }
    }

    public BrokerServerBuilder addConnectionEventProcessor(ConnectionEventType type, Supplier<ConnectionEventProcessor> processor) {
        this.connectionEventTypeProcessorMap.put(type, processor);
        return this;
    }

    public BrokerServerBuilder registerUserProcessor(Supplier<UserProcessor<?>> supplier) {
        this.userProcessors.add(supplier);
        return this;
    }

    private void defaultSetting() {
        //===================================注册连接处理器============================================
        Supplier<ConnectionEventProcessor> connectionEventProcessorSupplier = ConnectionEventBrokerProcessor::new;
        Supplier<ConnectionEventProcessor> closeConnectionEventProcessorSupplier = CloseConnectionEventBrokerProcessor::new;
        this
                .addConnectionEventProcessor(ConnectionEventType.CONNECT, connectionEventProcessorSupplier)
                .addConnectionEventProcessor(ConnectionEventType.CLOSE, closeConnectionEventProcessorSupplier);

        //===================================注册用户处理器============================================
        Supplier<UserProcessor<?>> registerProcessor = RegisterBrokerClientModuleMessageBrokerProcessor::new;
        Supplier<UserProcessor<?>> requestProcessor = RequestMessageBrokerProcessor::new;
        Supplier<UserProcessor<?>> responseProcessor = ResponseMessageBrokerProcessor::new;
        this
                //添加注册逻辑服信息处理器
                .registerUserProcessor(registerProcessor)
                //将对外服信息转发到逻辑服
                .registerUserProcessor(requestProcessor)
                //逻辑服的响应转发给对外服
                .registerUserProcessor(responseProcessor)
        ;
    }

    @Override
    public void aware(Object obj) {
        if(obj instanceof BrokerServerAware aware) {
            aware.setBrokerServer(brokerServer);
        }
        if(obj instanceof BrokerClientModulesAware aware){
            aware.setBrokerClientModules(brokerClientModules);
        }
    }
}
