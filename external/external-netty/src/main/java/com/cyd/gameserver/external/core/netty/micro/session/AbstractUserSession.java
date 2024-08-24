package com.cyd.gameserver.external.core.netty.micro.session;

import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.common.kit.attr.AttrOptions;
import com.cyd.gameserver.external.core.micro.session.UserChannelId;
import com.cyd.gameserver.external.core.micro.session.UserSession;
import com.cyd.gameserver.external.core.micro.session.UserSessionOption;
import com.cyd.gameserver.external.core.micro.session.UserSessionState;
import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
abstract class AbstractUserSession implements UserSession {
    /**
     * 动态属性
     */
    final AttrOptions options = new AttrOptions();

    Channel channel;

    long userId;

    @Setter
    UserSessionState state = UserSessionState.ACTIVE;

    UserChannelId userChannelId;

    public void employ(RequestMessage requestMessage) {
        HeadMetadata headMetadata = requestMessage.getHeadMetadata();
        // 设置请求用户的id
        headMetadata.setUserId(this.userId);

        this.ifPresent(UserSessionOption.externalJoin, externalJoin -> headMetadata.setStick(externalJoin.getCode()));

        if (!this.isVerifyIdentity()) {
            // 只有没进行验证的，才给 userChannelId
            String channelId = this.userChannelId.channelId();
            // 一般指用户的 channelId （来源于对外服的 channel 长连接）
            headMetadata.setChannelId(channelId);
        }

        // 设置用户绑定的游戏逻辑服 id
        this.ifPresent(UserSessionOption.bindingLogicServerIdArray, headMetadata::setBindingLogicServerIds);

        // 如果 headMetadata 的 attachmentData 不为 null，通常是开发者在其他地方给 attachmentData 设置了值，框架就不管了。
        if (Objects.isNull(headMetadata.getAttachmentData())) {
            // 将 UserSession attachment 的值设置到 HeadMetadata attachmentData 中
            this.ifPresent(UserSessionOption.attachment, headMetadata::setAttachmentData);
        }
    }

    @Override
    public void setUserId(long userId){
        this.userId = userId;
        //设置已认证
        this.option(UserSessionOption.verifyIdentify, true);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean isVerifyIdentity() {
        return this.optionValue(UserSessionOption.verifyIdentify, false);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userChannelId);
    }

    @Override
    public boolean equals(Object obj) {
        if(userChannelId == obj) {
            return true;
        }

        if(!(obj instanceof AbstractUserSession that)) {
            return false;
        }

        return Objects.equals(userChannelId, that.userChannelId);
    }
}
