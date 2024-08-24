package com.cyd.gameserver.action.skeleton.core.flow.interal;

import com.cyd.gameserver.action.skeleton.core.flow.ResponseMessageCreate;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;

public class DefaultResponseMessageCreate implements ResponseMessageCreate {
    @Override
    public ResponseMessage createResponseMessage() {
        return new ResponseMessage();
    }
}
