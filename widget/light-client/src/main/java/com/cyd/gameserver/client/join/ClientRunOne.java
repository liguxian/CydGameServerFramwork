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
package com.cyd.gameserver.client.join;

import com.cyd.gameserver.client.ClientConnectOption;
import com.cyd.gameserver.client.user.ClientUser;
import com.cyd.gameserver.client.user.DefaultClientUser;
import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.common.kit.InternalKit;
import com.cyd.gameserver.common.kit.PresentKit;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 渔民小镇
 * @date 2023-07-04
 */
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j(topic = LogName.CommonStdout)
public final class ClientRunOne {
    ClientUser clientUser;

    /** 服务器连接端口 */
    int connectPort = ExternalGlobalConfig.externalPort;
    String connectAddress = "127.0.0.1";

    ExternalJoinEnum joinEnum = ExternalJoinEnum.TCP;
    ClientConnectOption option;

    public void startup() {
        if (Objects.isNull(this.clientUser)) {
            this.clientUser = new DefaultClientUser();
        }

        ClientConnectOption option = getOption();

        ClientConnect clientConnect = ClientConnects.getClientConnect(joinEnum);
        if (Objects.isNull(clientConnect)) {
            log.error("连接方式 {} 没有对应的实现类", joinEnum);
            return;
        }

        InternalKit.execute(() -> clientConnect.connect(option));

        try {
            System.out.println("启动成功");
            log.info("启动成功");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private ClientConnectOption getOption() {

        if (Objects.isNull(option)) {
            option = new ClientConnectOption();
        }

        PresentKit.ifNull(option.getSocketAddress(), () -> {
            InetSocketAddress socketAddress = new InetSocketAddress(connectAddress, connectPort);
            option.setSocketAddress(socketAddress);
        });

        PresentKit.ifNull(option.getClientUser(), () -> option.setClientUser(clientUser));

        return option;
    }

    public static void main(String[] args) {
        new ClientRunOne().startup();
    }
}
