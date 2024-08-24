package com.cyd.gameserver.bolt.core.message;

import java.io.Serial;
import java.io.Serializable;

/**
 * bolt RpcClient.startup 后，需要发送消息才会建立连接
 * <pre>
 *     这里发送一个空消息
 * </pre>
 *
 * @author 渔民小镇
 * @date 2022-05-16
 */
public class BrokerClientItemConnectMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1148652635062833923L;
}
