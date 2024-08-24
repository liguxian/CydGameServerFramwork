package com.cyd.gameserver.action.skeleton.protocol.wrapper;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.List;

/**
 * byte数组的list包装类
 */
@ProtobufClass
public class ByteValueList {

    @Protobuf(fieldType = FieldType.BYTES, order = 1)
    public List<byte[]> values;

    public static ByteValueList of(List<byte[]> values){
        ByteValueList byteValueList = new ByteValueList();
        byteValueList.values = values;
        return byteValueList;
    }
}
