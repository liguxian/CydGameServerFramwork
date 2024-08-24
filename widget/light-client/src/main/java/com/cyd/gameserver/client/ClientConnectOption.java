package com.cyd.gameserver.client;

import com.cyd.gameserver.client.user.ClientUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.net.InetSocketAddress;

/**
 * 连接参数
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientConnectOption {
    String wsUrl;
    InetSocketAddress socketAddress;
    ClientUser clientUser;
}
