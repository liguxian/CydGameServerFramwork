package com.cyd.gameserver.action.skeleton.core.flow.interal;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.cyd.gameserver.action.skeleton.core.flow.ActionMethodParamParser;
import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;
import com.cyd.gameserver.action.skeleton.core.flow.parser.MethodParser;
import com.cyd.gameserver.action.skeleton.core.flow.parser.MethodParsers;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;

public class DefaultActionMethodParamParser implements ActionMethodParamParser {
    @Override
    public Object[] listParam(FlowContext flowContext) {
        ActionCommand actionCommand = flowContext.getActionCommand();
        if (!actionCommand.isMethodHasParam()) {
            return new Object[0];
        }

        RequestMessage request = flowContext.getRequest();

        ActionCommand.ParamInfo[] paramInfos = actionCommand.getParamInfos();
        Object[] params = new Object[paramInfos.length];

        for (int i = 0; i < paramInfos.length; i++) {
            ActionCommand.ParamInfo paramInfo = paramInfos[i];
            Class<?> actualTypeArgumentClazz = paramInfo.getActualTypeArgumentClazz();
            if (FlowContext.class.isAssignableFrom(actualTypeArgumentClazz)) {
                params[i] = flowContext;
            }

            MethodParser methodParser = MethodParsers.getMethodParser(paramInfo);
            params[i] = methodParser.parseParam(request.getData(), paramInfo);
        }

        return params;
    }
}
