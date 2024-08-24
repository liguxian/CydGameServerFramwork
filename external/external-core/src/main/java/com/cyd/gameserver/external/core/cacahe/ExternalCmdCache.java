package com.cyd.gameserver.external.core.cacahe;

import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.external.core.message.ExternalMessage;

/**
 * 将逻辑服的响应缓存，再次请求同一cmd时直接使用
 */
public interface ExternalCmdCache {

    ExternalMessage getCache(ExternalMessage externalMessage);

    /**
     * 将逻辑服响应ResponseMessage转为对外服响应ExternalMessage并缓存
     * @param responseMessage
     */
    void addCacheData(ResponseMessage responseMessage);
}
