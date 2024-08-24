package com.cyd.gameserver.external.core.broker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.bolt.core.common.AbstractAsyncUserProcessor;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.aware.ExternalCoreSettingAware;
import com.cyd.gameserver.external.core.aware.UserSessionsAware;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import com.cyd.gameserver.external.core.kit.ExternalKit;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import com.cyd.gameserver.external.core.micro.session.UserChannelId;
import com.cyd.gameserver.external.core.micro.session.UserSession;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j(topic = LogName.MsgTransferTopic)
public class ResponseMessageExternalProcessor extends AbstractAsyncUserProcessor<ResponseMessage> implements UserSessionsAware {

    final UserChannelId emptyUserChannelId = new UserChannelId("empty");
    @Setter
    UserSessions<?, ?> userSessions;

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, ResponseMessage responseMessage) {
        if (GlobalConfig.isExternalLog()) {
            log.info("接收来自网关的响应 {}", responseMessage);
        }

        ExternalMessage externalMessage = ExternalKit.convertExternalMessage(responseMessage);

        HeadMetadata headMetadata = responseMessage.getHeadMetadata();
        UserSession userSession;
        long userId = headMetadata.getUserId();
        if (userId > 0) {
            userSession = userSessions.getUserSession(userId);
        } else {
            String channelId = headMetadata.getChannelId();
            final UserChannelId userChannelId = Objects.nonNull(channelId) ? new UserChannelId(channelId) : emptyUserChannelId;
            userSession = userSessions.getUserSession(userChannelId);
        }

        if (Objects.nonNull(userSession)) {
            //相应给用户
            userSession.writeAndFlush(externalMessage);
        }

        //添加缓存
        if (headMetadata.getCacheCondition() != 0) {
            ExternalGlobalConfig.externalCmdCache.addCacheData(responseMessage);
        }
    }

    @Override
    public String interest() {
        return ResponseMessage.class.getName();
    }

}
