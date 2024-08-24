package com.cyd.gameserver.client.user;

import com.cyd.gameserver.action.skeleton.core.CmdInfo;
import com.cyd.gameserver.action.skeleton.core.codec.ProtoDataCodec;
import com.cyd.gameserver.client.command.CallbackDelegate;
import com.cyd.gameserver.client.command.CommandResult;
import com.cyd.gameserver.client.command.RequestCommand;
import com.cyd.gameserver.client.proto.Test1;
import com.cyd.gameserver.common.kit.CmdKit;
import com.cyd.gameserver.external.core.kit.ExternalKit;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import com.cyd.gameserver.external.core.message.ExternalMessageCmdCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 玩家通信 channel
 * <pre>
 *     发送请求，接收服务器响应
 * </pre>
 *
 * @author 渔民小镇
 * @date 2023-07-13
 */
@Slf4j
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientUserChannel {

    final DefaultClientUser clientUser;

    ClientChannelRead channelRead = new DefaultChannelRead();

    public Consumer<ExternalMessage> clientChannel;

    /**
     * 目标 ip （服务器 ip）
     */
    public InetSocketAddress inetSocketAddress;

    /**
     * 请求回调
     * <pre>
     *     key : msgId
     * </pre>
     */
    final Map<Integer, RequestCommand> callbackMap = new NonBlockingHashMap<>();

    /**
     * 请求id自增
     */
    final AtomicInteger msgIdSeq = new AtomicInteger(1);

    public ClientUserChannel(DefaultClientUser clientUser) {
        this.clientUser = clientUser;
    }

    public void read(ExternalMessage externalMessage) {
        channelRead.read(externalMessage);
    }

    public void writeAndFlush(ExternalMessage externalMessage) {
        if (Objects.isNull(clientChannel)) {
            return;
        }

        clientChannel.accept(externalMessage);
    }

    public void execute(RequestCommand requestCommand) {
        int msgId = this.msgIdSeq.incrementAndGet();
        this.callbackMap.put(msgId, requestCommand);
        CmdInfo cmdInfo = CmdInfo.of(requestCommand.getCmdMerge());

        ExternalMessage externalMessage = ExternalKit.createExternalMessage(cmdInfo);
        externalMessage.setMsgId(msgId);

        Object requestData = requestCommand.getRequestData();
        Object data = "";
        if (Objects.nonNull(requestData)) {
            data = requestData;
            externalMessage.setData(ProtoDataCodec.encode(data));
        }

        long userId = clientUser.getUserId();
        log.info("玩家[{}] 发起【{}】请求 - [msgId:{}] {} {}"
                , userId
                , requestCommand.getTitle()
                , msgId
                , CmdKit.mergeToShort(cmdInfo.getCmdMerge())
                , data
        );

        this.writeAndFlush(externalMessage);
    }

    class DefaultChannelRead implements ClientChannelRead {

        @Override
        public void read(ExternalMessage externalMessage) {
            // 表示有异常消息;统一异常处理
            int responseStatus = externalMessage.getResponseStatus();
            int cmdMerge = externalMessage.getCmdMerge();
            CmdInfo cmdInfo = CmdInfo.of(cmdMerge);

            if (responseStatus != 0) {
                log.error("[错误码:{}] - [消息:{}] - {}", responseStatus, externalMessage.getValidMsg(), cmdInfo);
                return;
            }

            if (externalMessage.getCmdCode() == ExternalMessageCmdCode.idle) {
                log.info("接收服务器心跳回调 : {}", externalMessage);

                return;
            }

            CommandResult commandResult = new CommandResult(externalMessage);

            int msgId = externalMessage.getMsgId();
            RequestCommand requestCommand = callbackMap.get(msgId);
            // 有回调的，交给回调处理
            if(Objects.nonNull(requestCommand)) {
                CallbackDelegate callback = requestCommand.getCallback();
                callback.callback(commandResult);
            }
        }
    }
}
