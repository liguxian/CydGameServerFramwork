package com.cyd.gameserver.bolt.core.common;

import com.cyd.gameserver.bolt.core.aware.UserProcessorExecutorAware;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * 内部网络全局设置
 */
@UtilityClass
public class GlobalConfig {

    /** broker （游戏网关）默认端口 */
    public int brokerPort = 10200;
    /** bolt 消息发送超时时间 */
    public int timeoutMillis = 3000;
    public boolean openLog = true;
    /** true 开启对外服相关日志 */
    public boolean externalLog = false;
    /** true 开启请求响应相关日志 */
    public boolean requestResponseLog = false;
    /** UserProcessor 构建 Executor 的策略 */
    public UserProcessorExecutorStrategy userProcessorExecutorStrategy = new DefaultUserProcessorExecutorStrategy();

    public Executor getExecutor(UserProcessorExecutorAware userProcessorExecutorAware) {

        if (Objects.isNull(userProcessorExecutorStrategy)) {
            // 不使用任何策略
            return null;
        }

        return userProcessorExecutorStrategy.getExecutor(userProcessorExecutorAware);
    }

    public boolean isExternalLog() {
        return openLog && externalLog;
    }

    /**
     * 网关客户端注册时是否通知其他客户端
     */
    public boolean sendBrokerClientModuleMessage = true;

    public boolean isSendBrokerClientModuleMessage() {
        // 实验性功能
        return sendBrokerClientModuleMessage;
    }
}
