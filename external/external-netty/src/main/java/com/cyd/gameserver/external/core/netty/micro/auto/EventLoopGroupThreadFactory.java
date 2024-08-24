package com.cyd.gameserver.external.core.netty.micro.auto;

import com.cyd.gameserver.common.kit.concurrent.DaemonThreadFactory;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadFactory;

@UtilityClass
public class EventLoopGroupThreadFactory {

    public ThreadFactory bossThreadFactory(){
        return new DaemonThreadFactory("gameserver.com:external-netty-server-boss");
    }

    public ThreadFactory workerThreadFactory(){
        return new DaemonThreadFactory("gameserver.com:external-netty-server-worker");
    }
}
