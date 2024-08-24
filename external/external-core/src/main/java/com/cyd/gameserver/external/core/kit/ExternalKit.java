package com.cyd.gameserver.external.core.kit;

import com.alipay.remoting.rpc.RpcCommandType;
import com.cyd.gameserver.action.skeleton.core.CmdInfo;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import com.cyd.gameserver.external.core.message.ExternalMessageCmdCode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExternalKit {

    /**
     * 对外服消息转网关服消息
     * @param externalMessage
     * @param idHash
     * @return
     */
    public RequestMessage convertRequestMessage(ExternalMessage externalMessage, int idHash) {
        int cmdMerge = externalMessage.getCmdMerge();

        // 元信息
        HeadMetadata headMetadata = new HeadMetadata()
                .setCmdMerge(cmdMerge)
                .setRpcCommandType(RpcCommandType.REQUEST_ONEWAY)
                .setSourceClientId(idHash)
                .setMsgId(externalMessage.getMsgId())
                .setCustomData(externalMessage.getCustomData());

        byte[] data = externalMessage.getData();

        // 请求
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setHeadMetadata(headMetadata);
        requestMessage.setData(data);

        return requestMessage;
    }

    public ExternalMessage convertExternalMessage(ResponseMessage responseMessage) {
        HeadMetadata headMetadata = responseMessage.getHeadMetadata();

        // 游戏框架内置的协议， 与游戏前端相互通讯的协议
        ExternalMessage externalMessage = createExternalMessage();
        // 路由
        externalMessage.setCmdMerge(headMetadata.getCmdMerge());
        // 业务数据
        externalMessage.setData(responseMessage.getData());
        // 状态码
        externalMessage.setResponseStatus(responseMessage.getResponseStatus());
        // 验证信息（异常消息）
        externalMessage.setValidMsg(responseMessage.getValidatorMsg());
        // 消息标记号；由前端请求时设置，服务器响应时会携带上
        externalMessage.setMsgId(headMetadata.getMsgId());
        // 开发者自定义数据
        externalMessage.setCustomData(headMetadata.getCustomData());

        return externalMessage;
    }

    public ExternalMessage createExternalMessage() {
        // 游戏框架内置的协议， 与游戏前端相互通讯的协议
        ExternalMessage externalMessage = new ExternalMessage();
        // 请求命令类型: 0 心跳，1 业务
        externalMessage.setCmdCode(ExternalMessageCmdCode.biz);
        // 协议开关，用于一些协议级别的开关控制，比如 安全加密校验等。 : 0 不校验
        externalMessage.setProtocolSwitch(ExternalGlobalConfig.protocolSwitch);
        return externalMessage;
    }

    public ExternalMessage createExternalMessage(CmdInfo cmdInfo) {
        ExternalMessage externalMessage = ExternalKit.createExternalMessage();
        externalMessage.setCmdMerge(cmdInfo.getCmdMerge());
        return externalMessage;
    }
}
