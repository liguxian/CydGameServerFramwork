package com.cyd.gameserver.bolt.core.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.config.Configs;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.communication.BrokerClientContext;
import com.cyd.gameserver.action.skeleton.core.communication.CommunicationAggregationContext;
import com.cyd.gameserver.action.skeleton.protocol.processor.SimpleServerInfo;
import com.cyd.gameserver.bolt.core.GroupWith;
import com.cyd.gameserver.bolt.core.aware.AwareInject;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.common.processor.hook.ClientProcessorHooks;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 逻辑服（网关的客户端聚合类）
 */
@Slf4j
@Setter(AccessLevel.PROTECTED)
@Getter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClient implements BrokerClientContext, GroupWith {

    /**
     * 服务器唯一标识
     */
    String id;

    /**
     * 逻辑服标签 （tag 相当于归类）
     * <pre>
     *     用于逻辑服的归类
     *     假设逻辑服： 战斗逻辑服 启动了两台或以上，为了得到启动连接的逻辑服，我们可以通过 tag 在后台查找
     *     相同的逻辑服一定要用相同的 tag
     *
     *     注意，如果没设置这个值，会使用 this.appName 的值
     * </pre>
     */
    String tag;

    /**
     * 模块名
     */
    String appName;

    /**
     * 逻辑服状态
     */
    int status;

    /**
     * 连接 broker （游戏网关） 的地址
     */
    BrokerAddress brokerAddress;

    /**
     * 逻辑服类型
     */
    BrokerClientType brokerClientType = BrokerClientType.LOGIC;

    /**
     * 逻辑服
     */
    BarSkeleton barSkeleton;

    /**
     * 消息发送超时时间
     */
    int timeoutMillis = GlobalConfig.timeoutMillis;

    /**
     * 简单的服务器信息
     */
    SimpleServerInfo simpleServerInfo;

    /**
     * BrokerClientItem管理器
     */
    BrokerClientItemManager brokerClientItemManager;

    /**
     * rpc连接事件
     */
    Map<ConnectionEventType, Supplier<ConnectionEventProcessor>> connectionEventProcessorMap;

    List<Supplier<UserProcessor<?>>> userProcessors;

    /**
     * 逻辑服，对外服的模块信息（发给网关的）
     */
    BrokerClientModuleMessage brokerClientModuleMessage;

    /**
     * 请求处理钩子
     */
    ClientProcessorHooks clientProcessorHooks;

    /**
     * aware注入扩展
     *
     * @param address
     */
    AwareInject awareInject;

    AtomicBoolean initAtomic = new AtomicBoolean(false);

    /**
     * 进程号
     */
    int withNo;

    BrokerClient() {
        // 开启 bolt 重连, 通过系统属性来开和关，如果一个进程有多个 RpcClient，则同时生效
        System.setProperty(Configs.CONN_MONITOR_SWITCH, "true");
        System.setProperty(Configs.CONN_RECONNECT_SWITCH, "true");
    }

    public static BrokerClientBuilder builder() {
        return new BrokerClientBuilder();
    }

    /**
     * 连接网关
     */
    public void init() {
        //没初始化才初始化
        if (initAtomic.compareAndSet(false, true)) {
            //初始化网关client管理器，并连接一个网关
            initBrokerClientItemManager();
        }
    }

    public void initBrokerClientItemManager() {
        if (Objects.isNull(brokerClientItemManager)) {
            brokerClientItemManager = new BrokerClientItemManager();
        }

        brokerClientItemManager
                .setBrokerClient(this)
                .setBrokerAddress(brokerAddress)
                .setConnectionEventProcessorMap(connectionEventProcessorMap)
                .setUserProcessors(userProcessors)
                .setTimeoutMillis(timeoutMillis)
        ;

        brokerClientItemManager.init();
    }

    public BrokerClientItem next() {
        return brokerClientItemManager.next();
    }

    @Override
    public SimpleServerInfo getSimpleServerInfo() {
        return simpleServerInfo;
    }

    /**
     * 给网关响应请求处理结果
     * @param responseObject 响应对象
     */
    @Override
    public void sendResponse(Object responseObject) {
        try {
            BrokerClientItem next = next();
            next.oneway(responseObject);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void oneway(Object request) throws Exception {
        next().oneway(request);
    }

    @Override
    public CommunicationAggregationContext getCommunicationAggregationContext() {
        return brokerClientItemManager.next();
    }

    public Object invokeSync(final Object request, final int timeoutMillis) throws RemotingException, InterruptedException {
        BrokerClientItem nextClient = next();
        return nextClient.invokeSync(request, timeoutMillis);
    }

    public Object invokeSync(final Object request) throws RemotingException, InterruptedException {
        return invokeSync(request, timeoutMillis);
    }

    @Override
    public void setWithNo(int withNo) {
        this.withNo = withNo;
    }
}
