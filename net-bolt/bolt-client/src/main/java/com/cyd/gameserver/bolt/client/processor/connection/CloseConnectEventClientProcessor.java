/*
 * ioGame
 * Copyright (C) 2021 - 2023  渔民小镇 （262610965@qq.com、luoyizhu@gmail.com） . All Rights Reserved.
 * # iohao.com . 渔民小镇
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.cyd.gameserver.bolt.client.processor.connection;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import com.alipay.remoting.ConnectionEventType;
import com.cyd.gameserver.bolt.core.aware.BrokerClientItemAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientItem;
import com.cyd.gameserver.bolt.core.client.BrokerClientItemManager;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.common.consts.LogName;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 渔民小镇
 * @date 2022-05-14
 */
@Slf4j(topic = LogName.ConnectionTopic)
public class CloseConnectEventClientProcessor implements ConnectionEventProcessor, BrokerClientItemAware {
    private final AtomicBoolean dicConnected = new AtomicBoolean();
    private final AtomicInteger disConnectTimes = new AtomicInteger();
    @Setter
    BrokerClientItem brokerClientItem;

    @Override
    public void onEvent(String remoteAddress, Connection conn) {
        if (GlobalConfig.openLog) {
            log.info("网关断开 ConnectionEventType:【{}】 remoteAddress:【{}】，Connection:【{}】",
                    ConnectionEventType.CLOSE, remoteAddress, conn
            );
        }

        Objects.requireNonNull(conn);
        dicConnected.set(true);
        disConnectTimes.incrementAndGet();

        //  这里要断开与 broker （游戏网关）的连接
        BrokerClient brokerClient = brokerClientItem.getBrokerClient();
        BrokerClientItemManager brokerClientItemManager = brokerClient.getBrokerClientItemManager();


        //  在集群时，需要移除与当前 broker （游戏网关）连接的 brokerClientItem
        brokerClientItemManager.remove(brokerClientItem);

        if (GlobalConfig.openLog) {
            log.info("网关断开 ConnectionEventType:【{}】 remoteAddress:【{}】，网关连接数量:【{}】",
                    ConnectionEventType.CLOSE, remoteAddress, brokerClientItemManager.countActiveItem()
            );
        }
    }

    public boolean isDisConnected() {
        return this.dicConnected.get();
    }

    public int getDisConnectTimes() {
        return this.disConnectTimes.get();
    }

    public void reset() {
        this.disConnectTimes.set(0);
        this.dicConnected.set(false);
    }
}
