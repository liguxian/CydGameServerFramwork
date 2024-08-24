package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;

/**
 *业务框架处理器
 */
public interface Handler {

    /**
     * 处理一个action请求
     *
     * @param flowContext flowContext
     * @return 如果返回 false 就不交给下一个链进行处理. 全剧终了.
     */
    boolean handler(FlowContext flowContext);
}
