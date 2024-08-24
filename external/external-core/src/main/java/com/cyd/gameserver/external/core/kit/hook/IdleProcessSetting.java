package com.cyd.gameserver.external.core.kit.hook;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.TimeUnit;

/**
 * 心跳设置
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdleProcessSetting {
    /**
     * 心跳整体时间
     */
    long idleTime = 300;
    /**
     * 读 - 心跳时间
     */
    long readerIdleTime = idleTime;
    /**
     * 写 - 心跳时间
     */
    long writerIdleTime = idleTime;
    /**
     * all - 心跳时间
     */
    long allIdleTime = idleTime;
    /**
     * 心跳时间单位 - 默认秒单位
     */
    TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * true : 响应心跳给客户端
     */
    boolean pong = true;
    /**
     * 心跳钩子
     */
    IdleHook<?> idleHook;

    /**
     * 心跳整体时间设置包括：readerIdleTime、writerIdleTime、allIdleTime
     *
     * @param idleTime 整体时间
     * @return this
     */
    public IdleProcessSetting setIdleTime(long idleTime) {
        this.idleTime = idleTime;
        this.readerIdleTime = idleTime;
        this.writerIdleTime = idleTime;
        this.allIdleTime = idleTime;
        return this;
    }
}
