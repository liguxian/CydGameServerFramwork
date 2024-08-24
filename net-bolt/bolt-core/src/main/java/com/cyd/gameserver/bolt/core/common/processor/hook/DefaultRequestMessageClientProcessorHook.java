package com.cyd.gameserver.bolt.core.common.processor.hook;

import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.common.kit.ExecutorKit;
import com.cyd.gameserver.common.kit.concurrent.ThreadCreator;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class DefaultRequestMessageClientProcessorHook implements RequestMessageClientProcessorHook {

    final Executor[] executors;

    final int executorLength;

    public DefaultRequestMessageClientProcessorHook() {
        executorLength = availableProcessors2n();
        executors = new Executor[executorLength];
        for (int i = 0; i < executorLength; i++) {
            String threadName = String.format("RequestMessage-%s-%s", executorLength, i + 1);
            executors[i] = ExecutorKit.newSingleThreadExecutor(new TheThreadFactory(threadName));
        }
    }

    @Override
    public void processLogic(BarSkeleton barSkeleton, FlowContext flowContext) {
        long userId = flowContext.getUserId();

        if(userId == 0) {
            HeadMetadata headMetadata = flowContext.getRequest().getHeadMetadata();
            userId = Optional.ofNullable(headMetadata.getChannelId())
                    .map(String::hashCode)
                    .map(Math::abs)
                    .orElseGet(headMetadata::getCmdMerge);
        }

        //根据userId获取executor
        int index = (int) (userId & (executorLength - 1));
        executors[index].execute(() -> barSkeleton.handle(flowContext));
    }

    private int availableProcessors2n() {
        int n = Runtime.getRuntime().availableProcessors();
        n |= (n >> 1);
        n |= (n >> 2);
        n |= (n >> 4);
        n |= (n >> 8);
        n |= (n >> 16);
        return (n + 1) >> 1;
    }

    private static class TheThreadFactory extends ThreadCreator implements ThreadFactory {

        public TheThreadFactory(String threadNamePrefix) {
            super(threadNamePrefix);
            setDaemon(true);
        }

        @Override
        public Thread newThread(Runnable r) {
            return createThread(r);
        }

        @Override
        protected String nextThreadName() {
            return this.getThreadNamePrefix();
        }
    }
}
