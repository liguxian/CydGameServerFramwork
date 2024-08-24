package com.cyd.gameserver.action.skeleton.core.communication;

import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;

import java.util.Collection;

/**
 * 广播通讯上下文
 */
public interface BroadcastContext {

    /**
     * 广播消息给单个用户
     *
     * @param responseMessage
     * @param userId
     */
    void broadcast(ResponseMessage responseMessage, long userId);

    /**
     * 广播消息给指定用户
     */
    void broadcast(ResponseMessage responseMessage, Collection<Long> userIds);

    /**
     * 广播消息给全体用户
     */
    void broadcast(ResponseMessage responseMessage);
}
