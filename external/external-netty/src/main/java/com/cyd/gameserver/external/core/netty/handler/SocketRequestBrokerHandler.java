package com.cyd.gameserver.external.core.netty.handler;

import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.bolt.core.aware.BrokerClientAware;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.message.BrokerClientModuleMessage;
import com.cyd.gameserver.common.consts.LogName;
import com.cyd.gameserver.external.core.aware.UserSessionsAware;
import com.cyd.gameserver.external.core.kit.ExternalKit;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import com.cyd.gameserver.external.core.micro.session.UserSessions;
import com.cyd.gameserver.external.core.netty.micro.session.SocketUserSession;
import com.cyd.gameserver.external.core.netty.micro.session.SocketUserSessions;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j(topic = LogName.ExternalTopic)
public class SocketRequestBrokerHandler extends SimpleChannelInboundHandler<ExternalMessage> implements UserSessionsAware, BrokerClientAware {

    SocketUserSessions userSessions;

    @Setter
    BrokerClient brokerClient;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ExternalMessage externalMessage) {

        //将ExternalMessage转成RequestMessage
        RequestMessage requestMessage = convertRequestMessage(externalMessage);

        //给请求加一些用户的信息
        SocketUserSession userSession = userSessions.getUserSession(channelHandlerContext);
        userSession.employ(requestMessage);

        try {
            // 请求游戏网关，在由网关转到具体的业务逻辑服
            brokerClient.oneway(requestMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void setUserSessions(UserSessions<?, ?> userSessions) {
        this.userSessions = (SocketUserSessions) userSessions;
    }

    /**
     * ExternalMessage转RequestMessage
     */
    private RequestMessage convertRequestMessage(ExternalMessage externalMessage) {

        //获取当前brokerClient的id
        BrokerClientModuleMessage brokerClientModuleMessage = brokerClient.getBrokerClientModuleMessage();
        int idHash = brokerClientModuleMessage.getIdHash();

        return ExternalKit.convertRequestMessage(externalMessage, idHash);
    }
}
