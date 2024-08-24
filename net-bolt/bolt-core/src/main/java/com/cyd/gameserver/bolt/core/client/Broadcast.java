package com.cyd.gameserver.bolt.core.client;

import com.alipay.remoting.exception.RemotingException;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.bolt.core.message.BroadcastMessage;
import com.cyd.gameserver.bolt.core.message.BroadcastOrderMessage;
import com.cyd.gameserver.common.consts.LogName;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 对BrokerClientItem的广播相关操作进行封装
 *
 * @param brokerClientItem
 */
@Slf4j(topic = LogName.MsgTransferTopic)
public record Broadcast(BrokerClientItem brokerClientItem) {

    void internalBroadcast(BroadcastMessage broadcastMessage) {
        try {
            brokerClientItem.oneway(broadcastMessage);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 广播消息给单个用户
     *
     * @param responseMessage
     * @param userId
     */
    public void broadcast(ResponseMessage responseMessage, long userId) {
        HeadMetadata headMetadata = responseMessage.getHeadMetadata();
        headMetadata.setUserId(userId);

        BroadcastMessage broadcastMessage = new BroadcastMessage()
                .setResponseMessage(responseMessage);

        internalBroadcast(broadcastMessage);
    }

    /**
     * 广播消息给指定用户
     */
    public void broadcast(ResponseMessage responseMessage, Collection<Long> userIds) {
        BroadcastMessage broadcastMessage = new BroadcastMessage()
                .setResponseMessage(responseMessage)
                .setUserIdList(userIds);

        internalBroadcast(broadcastMessage);
    }

    /**
     * 广播消息给全体用户
     */
    public void broadcast(ResponseMessage responseMessage) {
        BroadcastMessage broadcastMessage = new BroadcastMessage()
                .setResponseMessage(responseMessage)
                .setBroadcastAll(true);

        internalBroadcast(broadcastMessage);
    }

    /**
     * 广播消息给单个用户
     *
     * @param responseMessage
     * @param userId
     */
    public void broadcastOrder(ResponseMessage responseMessage, long userId) {
        HeadMetadata headMetadata = responseMessage.getHeadMetadata();
        headMetadata.setUserId(userId);

        BroadcastOrderMessage broadcastOrderMessage = new BroadcastOrderMessage();
        broadcastOrderMessage.setResponseMessage(responseMessage);

        internalBroadcast(broadcastOrderMessage);
    }

    /**
     * 广播消息给指定用户
     */
    public void broadcastOrder(ResponseMessage responseMessage, Collection<Long> userIds) {
        BroadcastOrderMessage broadcastOrderMessage = new BroadcastOrderMessage();
        broadcastOrderMessage.setResponseMessage(responseMessage);
        broadcastOrderMessage.setUserIdList(userIds);

        internalBroadcast(broadcastOrderMessage);
    }

    /**
     * 广播消息给全体用户
     */
    public void broadcastOrder(ResponseMessage responseMessage) {
        BroadcastOrderMessage broadcastOrderMessage = new BroadcastOrderMessage();
        broadcastOrderMessage.setResponseMessage(responseMessage);
        broadcastOrderMessage.setBroadcastAll(true);

        internalBroadcast(broadcastOrderMessage);
    }
}
