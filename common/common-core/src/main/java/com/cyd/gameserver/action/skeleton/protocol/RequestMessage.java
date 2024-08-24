package com.cyd.gameserver.action.skeleton.protocol;

import java.io.Serial;

public sealed class RequestMessage extends BaseMessage permits SyncRequestMessage {
    @Serial
    private static final long serialVersionUID = 8564408386704453534L;
    public ResponseMessage createResponseMessage() {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setHeadMetadata(this.headMetadata);
        return responseMessage;
    }

    public void settingCommonAttr(final ResponseMessage responseMessage) {
        // response 与 request 使用的 headMetadata 为同一引用
        responseMessage.setHeadMetadata(this.headMetadata);
    }
}
