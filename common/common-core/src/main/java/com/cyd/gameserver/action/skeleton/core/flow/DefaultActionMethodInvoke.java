package com.cyd.gameserver.action.skeleton.core.flow;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.esotericsoftware.reflectasm.MethodAccess;

public class DefaultActionMethodInvoke implements ActionMethodInvoke{
    @Override
    public Object invoke(FlowContext flowContext) {
        final ActionCommand actionCommand = flowContext.getActionCommand();
        final Object actionController = flowContext.getActionController();
        Object[] methodParams = flowContext.getMethodParams();

        //方法下标
        int actionMethodIndex = actionCommand.getActionMethodIndex();
        //方法访问器
        MethodAccess actionMethodAccess = actionCommand.getActionMethodAccess();

        try {
            return actionMethodAccess.invoke(actionController, actionMethodIndex, methodParams);
        } catch (Throwable e) {
            // true 业务方法有异常
            flowContext.setError(true);

            ActionMethodExceptionProcess actionMethodExceptionProcess = flowContext.getBarSkeleton().getActionMethodExceptionProcess();
            // 把业务方法抛出的异常,交由异常处理类来处理
            return actionMethodExceptionProcess.processException(e);
        }
    }
}
