package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.flow.*;
import com.cyd.gameserver.action.skeleton.core.flow.interal.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 业务框架builder
 */
@Getter
@Setter
public class BarSkeletonBuilder {

    @Getter
    final BarSkeletonSetting setting = new BarSkeletonSetting();

    /**
     * handler 列表
     */
    final List<Handler> handlerList = new LinkedList<>();

    /**
     * actionController class列表
     */
    final List<Class<?>> actionControllerClassList = new LinkedList<>();

    /**
     * action方法参数解析器（请求到来时解析byte[]成参数）
     */
    ActionMethodParamParser actionMethodParamParser = new DefaultActionMethodParamParser();

    /**
     * FlowContext创建工厂
     */
    FlowContextFactory flowContextFactory = FlowContext::new;

    /**
     * 响应对象创建
     *
     * @return
     */
    ResponseMessageCreate responseMessageCreate = new DefaultResponseMessageCreate();

    /**
     * actionBean工厂
     * @return
     */
    ActionFactoryBean<Object> actionFactoryBean = new DefaultActionFactoryBean<>();

    /**
     * actionMethod异常处理器
     * @return
     */
    ActionMethodExceptionProcess actionMethodExceptionProcess = new DefaultActionMethodExceptionProcess();

    /**
     * actionMethod执行器
     * @return
     */
    ActionMethodInvoke actionMethodInvoke = new DefaultActionMethodInvoke();

    /**
     * actionMethod处理结果包装器
     * @return
     */
    ActionMethodResultWrap actionMethodResultWrap = new DefaultActionMethodResultWrap();

    /** action 执行完后，最后需要做的事。 一般用于将数据发送到 Broker（游戏网关） */
    ActionAfter actionAfter = new DefaultActionAfter();

    public BarSkeleton build() {

        // 如果没有配置 handler，那么使用默认的
        if (this.handlerList.isEmpty()) {
            this.handlerList.add(new ActionCommandHandler());
        }

        Handler[] handlers = new Handler[handlerList.size()];
        handlerList.toArray(handlers);

        BarSkeleton barSkeleton = new BarSkeleton(handlers)
                .setActionMethodParamParser(actionMethodParamParser)
                .setFlowContextFactory(flowContextFactory)
                .setResponseMessageCreate(responseMessageCreate)
                .setActionFactoryBean(actionFactoryBean)
                .setActionMethodExceptionProcess(actionMethodExceptionProcess)
                .setActionMethodInvoke(actionMethodInvoke)
                .setActionMethodResultWrap(actionMethodResultWrap)
                .setActionAfter(actionAfter);

        buildActionCommand(barSkeleton);

        return barSkeleton;
    }

    public BarSkeletonBuilder addActionController(Class<?> actionController) {
        actionControllerClassList.add(actionController);
        return this;
    }

    /**
     * 构建业务框架的所有ActionCommand
     */
    private void buildActionCommand(BarSkeleton barSkeleton) {
        ActionCommandParser actionCommandParser = new ActionCommandParser(setting)
                .buildAction(actionControllerClassList);

        ActionCommandRegions actionCommandRegions = actionCommandParser.getActionCommandRegions();

        // 将 ActionCommandRegions 命令域管理器，保存到业务框架中
        barSkeleton.setActionCommandRegions(actionCommandRegions);
    }
}
