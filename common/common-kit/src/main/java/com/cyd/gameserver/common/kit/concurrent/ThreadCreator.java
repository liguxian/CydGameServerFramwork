package com.cyd.gameserver.common.kit.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThreadCreator {

    //线程名称前缀
    String threadNamePrefix;

    //线程优先级
    int threadPriority = Thread.NORM_PRIORITY;

    //是否是守护线程
    boolean daemon = false;

    final AtomicInteger count = new AtomicInteger();

    //线程组
    ThreadGroup threadGroup;

    public void setThreadGroupName(String name) {
        threadGroup = new ThreadGroup(name);
    }

    public ThreadCreator(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Thread createThread(Runnable runnable) {
        Thread thread = new Thread(threadGroup, runnable, nextThreadName());
        thread.setDaemon(daemon);
        thread.setPriority(threadPriority);
        return thread;
    }

    protected String nextThreadName() {
        String format = "%s-%d";
        return String.format(format, threadNamePrefix, count.incrementAndGet());
    }
}
