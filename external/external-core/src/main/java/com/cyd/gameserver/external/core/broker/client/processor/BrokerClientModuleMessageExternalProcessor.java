package com.cyd.gameserver.external.core.broker.client.processor;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.bolt.core.common.AbstractAsyncUserProcessor;
import com.cyd.gameserver.bolt.core.common.GlobalConfig;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import com.cyd.gameserver.common.consts.LogName;
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

/**
 * 逻辑服上线通知处理器
 */
@Slf4j(topic = LogName.MsgTransferTopic)
public class BrokerClientModuleMessageExternalProcessor extends AbstractAsyncUserProcessor<BrokerClientModuleMessage> {

    @Override
    public void handleRequest(BizContext bizContext, AsyncContext asyncContext, BrokerClientModuleMessage brokerClientModuleMessage) {
        //添加该逻辑服的路由
    }

    @Override
    public String interest() {
        return BrokerClientModuleMessage.class.getName();
    }
}
