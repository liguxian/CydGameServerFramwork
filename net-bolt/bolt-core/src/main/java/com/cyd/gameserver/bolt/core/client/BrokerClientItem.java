package com.cyd.gameserver.bolt.core.client;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.config.BoltClientOption;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.cyd.gameserver.action.skeleton.core.communication.CommunicationAggregationContext;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.bolt.core.aware.AwareInject;
import com.cyd.gameserver.bolt.core.aware.BrokerClientAware;
import com.cyd.gameserver.bolt.core.aware.BrokerClientItemAware;
import com.cyd.gameserver.bolt.core.aware.UserProcessorExecutorAware;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.BrokerClientItemConnectMessage;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import com.cyd.gameserver.common.kit.CollKit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 与游戏网关broker相连的client 与游戏网关是1:1的关系
 */
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientItem implements CommunicationAggregationContext, AwareInject {
    public enum Status {
        /**
         * 活跃
         */
        ACTIVE,
        /**
         * 断开
         */
        DISCONNECT
    }

    final Broadcast broadcast = new Broadcast(this);

    String brokerAddress;

    final RpcClient rpcClient;

    /**
     * 与broker通讯的连接
     */
    Connection connection;

    /**
     * 连接超时时间
     */
    int timeoutMillis = GlobalConfig.timeoutMillis;

    Status status = Status.DISCONNECT;

    /**
     * aware注入扩展
     *
     * @param address
     */
    AwareInject awareInject;

    BrokerClient brokerClient;

    int brokerServerWithNo;

    public BrokerClientItem(String address) {
        brokerAddress = address;

        rpcClient = new RpcClient();

        //重连选项
        rpcClient.option(BoltClientOption.CONN_RECONNECT_SWITCH, true);
        rpcClient.option(BoltClientOption.CONN_MONITOR_SWITCH, true);
    }

    public Object invokeSync(final Object request, final int timeoutMillis) throws RemotingException, InterruptedException {
        return rpcClient.invokeSync(connection, request, timeoutMillis);
    }

    public Object invokeSync(final Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, timeoutMillis);
    }

    public void oneway(final Object request) throws RemotingException {
        this.rpcClient.oneway(connection, request);
    }

    void invokeWithCallback(Object request) throws RemotingException {
        this.rpcClient.invokeWithCallback(connection, request, null, timeoutMillis);
    }

    private void internalOneway(Object responseMessage) {
        try {
            rpcClient.oneway(connection, responseMessage);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 用于各服务器之前的 processor 通信
     *
     * @param message message
     */
    @Override
    public void invokeOneway(Object message) {
        internalOneway(message);
    }

    @Override
    public void broadcast(ResponseMessage responseMessage, Collection<Long> userIdList) {

        if (CollKit.isEmpty(userIdList)) {
            log.warn("广播无效 userIdList : {}", userIdList);
            return;
        }

        this.broadcast.broadcast(responseMessage, userIdList);
    }

    @Override
    public void broadcast(ResponseMessage responseMessage, long userId) {
        this.broadcast.broadcast(responseMessage, userId);
    }

    @Override
    public void broadcast(ResponseMessage responseMessage) {
        this.broadcast.broadcast(responseMessage);
    }

    @Override
    public void broadcastOrder(ResponseMessage responseMessage, Collection<Long> userIdList) {
        this.broadcast.broadcastOrder(responseMessage, userIdList);
    }

    @Override
    public void broadcastOrder(ResponseMessage responseMessage, long userId) {
        this.broadcast.broadcastOrder(responseMessage, userId);
    }

    @Override
    public void broadcastOrder(ResponseMessage responseMessage) {
        this.broadcast.broadcastOrder(responseMessage);
    }

    /**
     * rpc添加连接事件处理器
     *
     * @param connectionEventType
     * @param connectionEventProcessor
     */
    public void addConnectionEventProcessor(ConnectionEventType connectionEventType, ConnectionEventProcessor connectionEventProcessor) {
        aware(connectionEventProcessor);
        rpcClient.addConnectionEventProcessor(connectionEventType, connectionEventProcessor);
    }

    public void registerUserProcessor(UserProcessor<?> processor) {
        aware(processor);
        rpcClient.registerUserProcessor(processor);
    }

    @Override
    public void aware(Object obj) {
        if (Objects.nonNull(awareInject)) {
            awareInject.aware(obj);
        }

        if (obj instanceof BrokerClientItemAware aware) {
            aware.setBrokerClientItem(this);
        }

        if (obj instanceof BrokerClientAware aware) {
            aware.setBrokerClient(this.brokerClient);
        }

        if(obj instanceof UserProcessorExecutorAware aware && Objects.isNull(aware.getUserProcessorExecutor())) {
            aware.setUserProcessorExecutor(GlobalConfig.getExecutor(aware));
        }
    }

    public void startUp() {
        rpcClient.startup();
        send();
    }

    private void send() {
        BrokerClientItemConnectMessage clientItemConnectMessage = new BrokerClientItemConnectMessage();
        try {
            rpcClient.oneway(brokerAddress, clientItemConnectMessage);
        } catch (RemotingException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 将服务器信息注册到网关服
     */
    public void registerToBroker() {
        BrokerClientModuleMessage brokerClientModuleMessage = brokerClient.getBrokerClientModuleMessage();

        try {
            rpcClient.oneway(brokerAddress, brokerClientModuleMessage);

            TimeUnit.MILLISECONDS.sleep(100);
            this.status = Status.ACTIVE;
            this.brokerClient.getBrokerClientItemManager().resetElementSelector();

            this.withNo();
        } catch (RemotingException | InterruptedException e) {
            System.out.println(e.getMessage());
            log.error(e.getMessage(), e);
        }
    }

    private void withNo() {
        val withNo = brokerClient.getWithNo();
        if (withNo == 0 || brokerServerWithNo == 0) {
            brokerServerWithNo = 0;
            return;
        }

        val brokerClientItemManager = brokerClient.getBrokerClientItemManager();
        brokerClientItemManager.setBrokerServerWith(this);
    }
}
