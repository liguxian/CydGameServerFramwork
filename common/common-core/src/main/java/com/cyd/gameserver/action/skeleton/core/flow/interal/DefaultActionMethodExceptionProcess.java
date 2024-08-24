package com.cyd.gameserver.action.skeleton.core.flow.interal;

import com.cyd.gameserver.action.skeleton.core.exception.ActionErrorEnum;
import com.cyd.gameserver.action.skeleton.core.exception.MsgException;
import com.cyd.gameserver.action.skeleton.core.flow.ActionMethodExceptionProcess;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultActionMethodExceptionProcess implements ActionMethodExceptionProcess {
    @Override
    public MsgException processException(final Throwable e) {
        if(e instanceof MsgException msgException) {
            return msgException;
        }
        // 到这里，一般不是用户自定义的错误，很可能是开发者引入的第三方包或自身未捕获的错误等情况
        log.error(e.getMessage(), e);

        return new MsgException(ActionErrorEnum.systemOtherErrCode);
    }
}
