package com.cyd.gameserver.action.skeleton.core.flow;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.cyd.gameserver.action.skeleton.core.ActionCommandRegions;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.communication.ChannelContext;
import com.cyd.gameserver.action.skeleton.core.flow.attr.FlowAttr;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class FlowContextKit {

    /** rpc oneway request */
    static final byte REQUEST_ONEWAY = (byte) 0x02;

    public void employ(FlowContext flowContext) {
        BarSkeleton barSkeleton = flowContext.getBarSkeleton();
        RequestMessage request = flowContext.getRequest();
        HeadMetadata headMetadata = request.getHeadMetadata();

        //路由
        if(Objects.isNull(flowContext.getActionCommand())) {
            ActionCommandRegions actionCommandRegions = barSkeleton.getActionCommandRegions();
            ActionCommand actionCommand = actionCommandRegions.getActionCommand(headMetadata.getCmdMerge());
            flowContext.setActionCommand(actionCommand);
        }

        //响应对象
        if(Objects.isNull(flowContext.getResponse())) {
            ResponseMessageCreate responseMessageCreate = barSkeleton.getResponseMessageCreate();
            ResponseMessage responseMessage = responseMessageCreate.createResponseMessage();
            request.settingCommonAttr(responseMessage);

            flowContext.setResponse(responseMessage);
        }
    }

    public ChannelContext getChannelContext(FlowContext flowContext) {
        ResponseMessage response = flowContext.getResponse();
        HeadMetadata headMetadata = response.getHeadMetadata();

        byte rpcCommandType = headMetadata.getRpcCommandType();

        if (rpcCommandType == REQUEST_ONEWAY) {
            return flowContext.option(FlowAttr.brokerClientContext);
        } else {
            return flowContext.option(FlowAttr.channelContext);
        }
    }
}
