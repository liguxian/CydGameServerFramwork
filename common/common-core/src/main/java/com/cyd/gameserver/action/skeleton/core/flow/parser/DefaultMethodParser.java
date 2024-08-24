package com.cyd.gameserver.action.skeleton.core.flow.parser;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.cyd.gameserver.action.skeleton.core.codec.ProtoDataCodec;
import com.cyd.gameserver.action.skeleton.protocol.wrapper.ByteValueList;
import com.cyd.gameserver.common.kit.CollKit;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 默认action方法参数解析器
 */
public class DefaultMethodParser implements MethodParser{
    @Override
    public Class<?> getActualClazz(ActionCommand.MethodParamResultInfo methodParamResultInfo) {
        return methodParamResultInfo.getActualTypeArgumentClazz();
    }

    @Override
    public Object parseParam(byte[] data, ActionCommand.ParamInfo paramInfo) {
        //参数类型，如果参数是list，则为泛型类型
        Class<?> actualTypeArgumentClazz = paramInfo.getActualTypeArgumentClazz();

        if(paramInfo.isList()) {
            if(Objects.isNull(data)) {
                return Collections.emptyList();
            }

            ByteValueList byteValueList = ProtoDataCodec.decode(data, ByteValueList.class);
            if(CollKit.isEmpty(byteValueList.values)) {
                return  Collections.emptyList();
            }

            return byteValueList.values.stream()
                    .map(bytes -> ProtoDataCodec.decode(bytes, actualTypeArgumentClazz))
                    .toList();
        }

        if(Objects.isNull(data)) {
            return null;
        }

        return ProtoDataCodec.decode(data, actualTypeArgumentClazz);
    }

    @Override
    public Object parseResult(ActionCommand.ActionMethodReturnInfo actionMethodReturnInfo, Object methodResult) {

        if (actionMethodReturnInfo.isList()) {

            List<Object> list = (List<Object>) methodResult;

            ByteValueList byteValueList = new ByteValueList();
            byteValueList.values = list.stream()
                    .map(ProtoDataCodec::encode)
                    .collect(Collectors.toList());

            return byteValueList;
        }

        return methodResult;
    }

    @Override
    public boolean isCustomMethodParser() {
        return false;
    }

    private DefaultMethodParser(){

    }

    public static DefaultMethodParser me() {
        return Holder.ME;
    }

    private static class Holder{
        private static DefaultMethodParser ME = new DefaultMethodParser();
    }
}
