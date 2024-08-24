package com.cyd.gameserver.bolt.core.client;

import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.cyd.gameserver.bolt.core.loadbalance.ElementSelector;
import com.cyd.gameserver.bolt.core.loadbalance.ElementSelectorFactory;
import com.cyd.gameserver.bolt.core.loadbalance.RandomElementSelector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * broker的客户端管理器
 */
@Getter
@Setter
@Slf4j
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerClientItemManager {

    final Map<String, BrokerClientItem> brokerClientItemMap = new NonBlockingHashMap<>();

    /**
     * rpc连接事件
     */
    Map<ConnectionEventType, Supplier<ConnectionEventProcessor>> connectionEventProcessorMap;

    List<Supplier<UserProcessor<?>>> userProcessors;

    /**
     * 元素选择器工厂
     */
    ElementSelectorFactory<BrokerClientItem> elementSelectorFactory = RandomElementSelector::new;

    /**
     * 元素选择器
     */
    ElementSelector<BrokerClientItem> elementSelector;

    /**
     * broker地址
     */
    BrokerAddress brokerAddress;

    BrokerClient brokerClient;

    /**
     * 同进程的网关
     */
    BrokerClientItem brokerServerWith;

    /**
     * 消息发送超时时间
     */
    int timeoutMillis;

    public void init() {
        elementSelector = elementSelectorFactory.createElementSelector(Collections.emptyList());
        register(brokerAddress.getAddress());
    }

    /**
     * 新增连接
     *
     * @param address
     */
    public void register(String address) {
        BrokerClientItem brokerClientItem = new BrokerClientItem(address)
                .setTimeoutMillis(timeoutMillis)
                .setBrokerClient(brokerClient)
                .setAwareInject(brokerClient.getAwareInject());

        //添加连接事件
        connectionEventProcessorMap.forEach((type, supplier) -> {
            ConnectionEventProcessor connectionEventProcessor = supplier.get();
            brokerClientItem.addConnectionEventProcessor(type, connectionEventProcessor);
        });

        userProcessors.stream()
                .map(Supplier::get)
                .forEach(brokerClientItem::registerUserProcessor);


        //开始连接broker网关
        brokerClientItem.startUp();

        brokerClientItemMap.put(address, brokerClientItem);

        //重置选择器
        resetElementSelector();
    }

    /**
     * 重置选择器（当连接数量变化时使用）
     */
    void resetElementSelector(){
        List<BrokerClientItem> brokerClientItems = brokerClientItemMap.values().stream()
                .filter(brokerClientItem -> brokerClientItem.getStatus() == BrokerClientItem.Status.ACTIVE)
                .toList();
        elementSelector = elementSelectorFactory.createElementSelector(brokerClientItems);
    }

    public BrokerClientItem next() {

        return Objects.nonNull(brokerServerWith) ? brokerServerWith : elementSelector.next();
    }

    /**
     * 统计活跃连接
     */
    public int countActiveItem() {
        return (int) brokerClientItemMap.values().stream()
                .filter(brokerClientItem -> brokerClientItem.getStatus() == BrokerClientItem.Status.ACTIVE)
                .count();
    }

    /**
     * 移除连接（更改连接状态为断开）
     */
    public void remove(BrokerClientItem item) {
        item.setStatus(BrokerClientItem.Status.DISCONNECT);
        resetElementSelector();
    }

}
