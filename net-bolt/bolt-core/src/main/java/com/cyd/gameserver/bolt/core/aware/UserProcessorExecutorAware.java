package com.cyd.gameserver.bolt.core.aware;

import java.util.concurrent.Executor;

public interface UserProcessorExecutorAware {

    void setUserProcessorExecutor(Executor executor);

    Executor getUserProcessorExecutor();
}
