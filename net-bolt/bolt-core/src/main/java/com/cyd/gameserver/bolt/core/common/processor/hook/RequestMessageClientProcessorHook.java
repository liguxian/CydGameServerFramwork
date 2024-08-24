package com.cyd.gameserver.bolt.core.common.processor.hook;

import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;

public interface RequestMessageClientProcessorHook {

    /**
     * 钩子流程逻辑
     * <pre>
     *     通过业务框架把请求派发给指定的业务类（action）来处理
     *
     *     用于在 bolt 接收请求时，对该请求做一些类似线程编排的事
     *     当然，这个编排是由开发者自定义的
     * </pre>
     *
     * @param barSkeleton 业务框架
     * @param flowContext 业务框架 flow 上下文
     */
    void processLogic(BarSkeleton barSkeleton, FlowContext flowContext);
}
