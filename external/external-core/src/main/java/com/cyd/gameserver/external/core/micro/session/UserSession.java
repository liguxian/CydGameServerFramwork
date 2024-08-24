package com.cyd.gameserver.external.core.micro.session;

import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.common.kit.attr.AttrOptionDynamic;

public interface UserSession extends AttrOptionDynamic {

    /**
     * 是否在线
     * @return
     */
    boolean isActive();

    void setUserId(long userId);

    long getUserId();

    /**
     * 是否已认证
     * @return
     */
    boolean isVerifyIdentity();

    UserSessionState getState();

    /**
     * 获取channelId
     * @return
     */
    UserChannelId getUserChannelId();

    <T> T writeAndFlush(Object message);

    String getIp();

    /**
     * 给请求消息加上一些 user 自身的数据
     * <pre>
     *     如果开发者要扩展数据，可通过 {@link HeadMetadata#setAttachmentData(byte[])} 字段来扩展
     *     这些数据可以传递到逻辑服
     * </pre>
     *
     * @param requestMessage 请求消息
     */
    void employ(RequestMessage requestMessage);
}
