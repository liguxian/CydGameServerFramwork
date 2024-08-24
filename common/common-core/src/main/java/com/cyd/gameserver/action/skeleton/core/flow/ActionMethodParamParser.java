package com.cyd.gameserver.action.skeleton.core.flow;

/**
 * action方法参数解析（针对action方法的所有参数）
 */
public interface ActionMethodParamParser {

    /**
     * 参数解析
     *
     * @param flowContext flow 上下文
     * @return 参数列表 一定不为 null
     */
    Object[] listParam(final FlowContext flowContext);
}
