package com.cyd.gameserver.action.skeleton.core.flow;

import com.cyd.gameserver.action.skeleton.annotation.ActionController;
import com.cyd.gameserver.action.skeleton.annotation.ActionMethod;

/**
 * ActionMethod Invoke
 * <pre>
 *     调用业务层的方法 (即对外提供的方法)
 * </pre>
 *
 * @author 渔民小镇
 * @date 2021-12-20
 */
public interface ActionMethodInvoke {

    /**
     * 具体的业务方法调用
     * <pre>
     *     类有上有注解 {@link ActionController}
     *     方法有注解 {@link ActionMethod}
     *     只要有这两个注解的，就是业务类
     * </pre>
     *
     * @param flowContext flow 上下文
     * @return 返回值
     */
    Object invoke(FlowContext flowContext);
}
