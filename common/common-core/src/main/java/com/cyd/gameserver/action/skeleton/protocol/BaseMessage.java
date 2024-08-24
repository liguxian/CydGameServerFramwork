package com.cyd.gameserver.action.skeleton.protocol;

import com.cyd.gameserver.action.skeleton.core.codec.ProtoDataCodec;
import com.cyd.gameserver.action.skeleton.core.exception.MsgExceptionInfo;
import com.cyd.gameserver.common.consts.CommonConst;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 消息基类
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract sealed class BaseMessage implements Serializable permits RequestMessage, ResponseMessage {

    @Serial
    private static final long serialVersionUID = 562068269463876111L;

    /** 响应码: 0:成功, 其他表示有错误 */
    int responseStatus;
    /** 异常信息、JSR380 验证信息 */
    String validatorMsg;

    /** 元信息 */
    HeadMetadata headMetadata;

    /**
     * 业务数据的 class 信息
     * <pre>
     *     <a href="https://gitee.com/iohao/iogame/issues/I5G0FC">...</a>
     * </pre>
     */
    String dataClass;
    /** 实际请求的业务参数 byte[] */
    byte[] data;

    public BaseMessage setData(byte[] data) {
        this.data = data;
        return this;
    }

    public BaseMessage setData(Object data) {
        if (Objects.isNull(data)) {
            return this.setData(CommonConst.emptyBytes);
        }

        // 保存一下业务数据的 class
        this.dataClass = data.getClass().getName();
        byte[] bytes = ProtoDataCodec.encode(data);
        return this.setData(bytes);
    }

    /**
     * 设置验证的错误信息
     *
     * @param validatorMsg 错误信息
     * @return this
     */
    public BaseMessage setValidatorMsg(String validatorMsg) {
        if (validatorMsg != null) {
            this.validatorMsg = validatorMsg;
        }

        return this;
    }

    public BaseMessage setError(MsgExceptionInfo msgExceptionInfo) {
        this.responseStatus = msgExceptionInfo.getCode();
        this.validatorMsg = msgExceptionInfo.getMsg();
        return this;
    }

    /**
     * 是否有错误
     * <pre>
     *     this.errorCode != 0 表示有错误
     * </pre>
     *
     * @return true 有错误码
     */
    public boolean hasError() {
        return this.responseStatus != 0;
    }
}
