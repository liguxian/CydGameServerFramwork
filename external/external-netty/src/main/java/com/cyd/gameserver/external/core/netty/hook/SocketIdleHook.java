package com.cyd.gameserver.external.core.netty.hook;

import com.cyd.gameserver.external.core.kit.hook.IdleHook;
import io.netty.handler.timeout.IdleStateEvent;

public interface SocketIdleHook extends IdleHook<IdleStateEvent> {
}
