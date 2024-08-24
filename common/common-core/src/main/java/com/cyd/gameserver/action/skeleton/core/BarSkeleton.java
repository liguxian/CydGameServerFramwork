package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.flow.*;
import com.cyd.gameserver.common.kit.attr.AttrOptionDynamic;
import com.cyd.gameserver.common.kit.attr.AttrOptions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 业务框架
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
public class BarSkeleton implements AttrOptionDynamic {

    final AttrOptions options = new AttrOptions();

    /**
     * handler array
     */
    final Handler[] handlers;

    /**
     * action方法参数解析器（请求到来时解析byte[]成参数）
     */
    ActionMethodParamParser actionMethodParamParser;

    /**
     * 框架所有ActionCommand
     */
    ActionCommandRegions actionCommandRegions = new ActionCommandRegions();

    /**
     * FlowContext创建工厂
     */
    FlowContextFactory flowContextFactory;

    /**
     * 响应对象创建
     *
     * @return
     */
    ResponseMessageCreate responseMessageCreate;

    /**
     * actionBean工厂
     * @return
     */
    ActionFactoryBean<Object> actionFactoryBean;

    /**
     * actionMethod异常处理器
     * @return
     */
    ActionMethodExceptionProcess actionMethodExceptionProcess;

    /**
     * actionMethod执行器
     * @return
     */
    ActionMethodInvoke actionMethodInvoke;

    /**
     * actionMethod处理结果包装器
     * @return
     */
    ActionMethodResultWrap actionMethodResultWrap;

    /** action 执行完后，最后需要做的事。 一般用于将数据发送到 Broker（游戏网关） */
    ActionAfter actionAfter;

    BarSkeleton(Handler[] handlers) {
        this.handlers = handlers;
    }

    public static BarSkeletonBuilder newBuilder() {
        return new BarSkeletonBuilder();
    }

    /**
     * 业务框架处理入口
     * @param flowContext
     */
    public void handle(final FlowContext flowContext) {
        // 将业务框架设置到 FlowContext 中
        flowContext.setBarSkeleton(this);

        /*
         * 多次访问的变量，保存到局部变量，可以提升性能。
         * 把成员变量的访问变为局部变量的访问 。 通过栈帧访问（线程栈），不用每次从堆中得到成员变量
         *
         * 因为这段代码访问频繁，才这样做。常规下不需要这么做
         * 可以参考 HashMap 的 putVal 方法相关
         */
        var handlers = this.handlers;

        if (handlers.length == 1) {
            handlers[0].handler(flowContext);
            return;
        }

        for (Handler theHandler : handlers) {
            if (!theHandler.handler(flowContext)) {
                return;
            }
        }
    }
}
