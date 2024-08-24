package com.cyd.gameserver.action.skeleton.core.flow;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.CmdInfo;
import com.cyd.gameserver.action.skeleton.core.codec.ProtoDataCodec;
import com.cyd.gameserver.action.skeleton.core.flow.attr.FlowOption;
import com.cyd.gameserver.action.skeleton.core.flow.attr.FlowOptionDynamic;
import com.cyd.gameserver.action.skeleton.protocol.HeadMetadata;
import com.cyd.gameserver.action.skeleton.protocol.RequestMessage;
import com.cyd.gameserver.action.skeleton.protocol.ResponseMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 逻辑服请求过程上下文
 */
@Getter
@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlowContext implements FlowOptionDynamic {

    final Map<FlowOption<?>, Object> options = new HashMap<>();

    /** 业务框架 */
    BarSkeleton barSkeleton;
    /** command */
    ActionCommand actionCommand;
    /** 控制器类对象 */
    Object actionController;
    /** 请求对象 */
    RequestMessage request;
    /** 响应对象 */
    ResponseMessage response;
    /** 业务方法参数 */
    Object[] methodParams;
    /** 业务方法的返回值 */
    Object methodResult;

    long userId;
    /** true 业务方法有异常 */
    boolean error;

    /** true 执行 ActionAfter 接口 {@link ActionAfter} */
    boolean executeActionAfter = true;

    /**
     * 获取请求的cmdInfo
     * @return
     */
    public CmdInfo getCmdInfo(){
        HeadMetadata headMetadata = request.getHeadMetadata();
        return headMetadata.getCmdInfo();
    }

    /**
     * 元附加信息
     * <pre>
     *     一般是在游戏对外服中设置的一些附加信息
     *     这些信息会跟随请求来到游戏逻辑服中
     * </pre>
     *
     * @param clazz clazz
     * @param <T>   t
     * @return 元附加信息
     */
    public <T> T getAttachment(Class<T> clazz) {
        HeadMetadata headMetadata = request.getHeadMetadata();
        return (T) ProtoDataCodec.decode(headMetadata.getAttachmentData(), clazz);
    }

    /**
     * 设置请求结果
     * @param result
     * @return
     */
    public FlowContext setMethodResult(Object result) {
        if(Objects.nonNull(result)) {
            this.methodResult = result;
        }

        return this;
    }


}
