package com.cyd.gameserver.common.kit.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DaemonThreadFactory implements ThreadFactory {

    //线程名称前缀
    ThreadCreator threadCreator;

    public DaemonThreadFactory(String threadNamePrefix) {
        threadCreator = new ThreadCreator(threadNamePrefix);
        threadCreator.setDaemon(true);
    }

    @Override
    public Thread newThread(Runnable r) {
        return  threadCreator.createThread(r);
    }
}
