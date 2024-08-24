package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.flow.ActionAfter;
import com.cyd.gameserver.action.skeleton.core.flow.ActionMethodInvoke;
import com.cyd.gameserver.action.skeleton.core.flow.ActionMethodResultWrap;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;

public class DefaultActionCommandFlowExecute implements ActionCommandFlowExecute{
    @Override
    public void execute(FlowContext flowContext) {
        //业务框架
        BarSkeleton barSkeleton = flowContext.getBarSkeleton();

        //true 表示没有错误码 。如果在这里有错误码，一般是业务参数验证得到的错误 （即开启了业务框架的验证）
        boolean hasNotError = !flowContext.getResponse().hasError();
        if(hasNotError) {
            ActionCommand actionCommand = flowContext.getActionCommand();
            ActionFactoryBean<Object> actionFactoryBean = barSkeleton.getActionFactoryBean();
            Object actionControllerInstance = actionFactoryBean.getBean(actionCommand);
            flowContext.setActionController(actionControllerInstance);

            //方法执行器
            ActionMethodInvoke actionMethodInvoke = barSkeleton.getActionMethodInvoke();
            //获取方法执行结果
            Object result = actionMethodInvoke.invoke(flowContext);
            flowContext.setMethodResult(result);

            //包装处理结果（设置到Response中）
            ActionMethodResultWrap actionMethodResultWrap = barSkeleton.getActionMethodResultWrap();
            actionMethodResultWrap.wrap(flowContext);
        }

        if(flowContext.isExecuteActionAfter()){
            //后续处理器
            ActionAfter actionAfter = barSkeleton.getActionAfter();
            actionAfter.execute(flowContext);
        }
    }

    private DefaultActionCommandFlowExecute() {

    }

    public static DefaultActionCommandFlowExecute me() {
        return Holder.ME;
    }

    /** 通过 JVM 的类加载机制, 保证只加载一次 (singleton) */
    private static class Holder {
        static final DefaultActionCommandFlowExecute ME = new DefaultActionCommandFlowExecute();
    }
}
