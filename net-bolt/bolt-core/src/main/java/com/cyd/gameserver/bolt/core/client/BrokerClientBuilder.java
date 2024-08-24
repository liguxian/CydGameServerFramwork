package com.cyd.gameserver.bolt.core.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.cyd.gameserver.action.skeleton.core.ActionCommandRegions;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.protocol.processor.SimpleServerInfo;
import com.cyd.gameserver.bolt.core.aware.AwareInject;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.common.processor.hook.ClientProcessorHooks;
import com.cyd.gameserver.bolt.core.config.BrokerClientStatusConfig;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import com.cyd.gameserver.common.kit.HashKit;
import com.cyd.gameserver.common.kit.NetworkKit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;

import java.util.*;
import java.util.function.Supplier;

@Setter
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientBuilder {

    /**
     * rpc连接事件
     */
    final Map<ConnectionEventType, Supplier<ConnectionEventProcessor>> connectionEventProcessorMap = new NonBlockingHashMap<>();

    final List<Supplier<UserProcessor<?>>> userProcessors = new ArrayList<>();

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
    int status = BrokerClientStatusConfig.normal;

    /**
     * 连接 broker （游戏网关） 的地址
     */
    BrokerAddress brokerAddress;

    /**
     * 业务框架
     */
    BarSkeleton barSkeleton;

    /**
     * 逻辑服类型
     */
    BrokerClientType brokerClientType = BrokerClientType.LOGIC;

    /**
     * 消息发送超时时间
     */
    int timeoutMillis = GlobalConfig.timeoutMillis;

    /**
     * BrokerClientItem管理器
     */
    BrokerClientItemManager brokerClientItemManager;

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

    /**
     * 进程组号
     */
    int withNo;

    public BrokerClient build() {
        //设置一些属性值
        setDefaultValue();
        //服务器信息对象
        SimpleServerInfo simpleServerInfo = createSimpleServerInfo();

        BrokerClient brokerClient = new BrokerClient()
                .setId(id)
                .setAppName(appName)
                .setTag(tag)
                .setStatus(status)
                .setBrokerClientType(brokerClientType)
                .setBarSkeleton(barSkeleton)
                .setBrokerClientItemManager(brokerClientItemManager)
                .setBrokerAddress(brokerAddress)
                .setTimeoutMillis(timeoutMillis)
                .setBrokerClientModuleMessage(createBrokerClientModuleMessage())
                .setConnectionEventProcessorMap(connectionEventProcessorMap)
                .setUserProcessors(userProcessors)
                .setSimpleServerInfo(simpleServerInfo)
                .setClientProcessorHooks(clientProcessorHooks)
                .setAwareInject(awareInject);

        brokerClient.setWithNo(withNo);

        return brokerClient;
    }

    public BrokerClientBuilder addConnectionEventProcessor(ConnectionEventType type, Supplier<ConnectionEventProcessor> processorSupplier) {
        connectionEventProcessorMap.put(type, processorSupplier);
        return this;
    }

    public BrokerClientBuilder registerUserProcessor(Supplier<UserProcessor<?>> processorSupplier) {
        userProcessors.add(processorSupplier);
        return this;
    }

    private void setDefaultValue() {
        if (Objects.isNull(id)) {
            id = UUID.randomUUID().toString();
        }

        if (Objects.isNull(appName)) {
            throw new RuntimeException("必须设置 appName");
        }

        if (Objects.isNull(this.clientProcessorHooks)) {
            // 因为目前 clientProcess 的 hook 只有一个，暂时这样处理着
            this.clientProcessorHooks = new ClientProcessorHooks();
        }

        if (Objects.isNull(tag)) {
            tag = appName;
        }
    }

    public SimpleServerInfo createSimpleServerInfo() {
        return new SimpleServerInfo()
                .setId(id)
                .setName(appName)
                .setTag(tag)
                .setIdHash(HashKit.hash32(id))
                .setBrokerClientType(brokerClientType.name())
                .setStartTime(System.currentTimeMillis())
                .setStatus(status);
    }

    private BrokerClientModuleMessage createBrokerClientModuleMessage() {
        return new BrokerClientModuleMessage()
                .setId(id)
                .setName(appName)
                .setTag(tag)
                .setAddress(NetworkKit.LOCAL_IP)
                .setIdHash(HashKit.hash32(id))
                .setBrokerClientType(brokerClientType)
                .setCmdMergeList(cmdMergeList())
                .setStatus(status);
    }

    /**
     * 逻辑服的cmdMergeList
     *
     * @return
     */
    private List<Integer> cmdMergeList() {
        if (Objects.nonNull(barSkeleton)) {
            ActionCommandRegions actionCommandRegions = barSkeleton.getActionCommandRegions();
            return actionCommandRegions.listCmdMerge();
        }
        return Collections.emptyList();
    }
}
