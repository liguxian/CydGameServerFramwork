package com.cyd.gameserver.test.logic.battle.proto;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ProtobufClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Test1 {

    @Protobuf(fieldType = FieldType.STRING, order = 1)
    String msg;
}
