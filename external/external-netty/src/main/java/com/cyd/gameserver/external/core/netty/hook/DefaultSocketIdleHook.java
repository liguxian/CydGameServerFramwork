package com.cyd.gameserver.external.core.netty.hook;

import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.external.core.micro.session.UserSession;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * tcp，websocket心跳钩子
 */
@Slf4j(topic = LogName.CommonStdout)
public class DefaultSocketIdleHook implements SocketIdleHook {

    @Override
    public boolean callback(UserSession userSession, IdleStateEvent idleStateEvent) {
        IdleState state = idleStateEvent.state();
        if(state == IdleState.READER_IDLE){
            /* 读超时 */
            log.debug("READER_IDLE 读超时");
        } else if (state == IdleState.WRITER_IDLE) {
            /* 写超时 */
            log.debug("WRITER_IDLE 写超时");
        } else if (state == IdleState.ALL_IDLE) {
            /* 总超时 */
            log.debug("ALL_IDLE 总超时");
        }


        return true;
    }
}
