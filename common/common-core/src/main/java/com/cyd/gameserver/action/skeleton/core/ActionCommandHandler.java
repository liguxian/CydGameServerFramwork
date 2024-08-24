package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.flow.ActionMethodParamParser;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求处理器
 */
@Slf4j
public class ActionCommandHandler implements Handler {


    @Override
    public boolean handler(FlowContext flowContext) {
        try{
            //参数解析
            BarSkeleton barSkeleton = flowContext.getBarSkeleton();
            ActionMethodParamParser actionMethodParamParser = barSkeleton.getActionMethodParamParser();
            Object[] params = actionMethodParamParser.listParam(flowContext);
            flowContext.setMethodParams(params);

            // actionCommand 命令流程执行器
            DefaultActionCommandFlowExecute.me().execute(flowContext);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
        return false;
    }
}
