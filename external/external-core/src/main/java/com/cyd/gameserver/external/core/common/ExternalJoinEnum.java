package com.cyd.gameserver.external.core.common;

import lombok.Getter;

@Getter
public enum ExternalJoinEnum {

    TCP("tcp", 1),
    UDP("udp", 2),
    WEBSOCKET("websocket", 3)
    ;

    private String name;

    private int code;

    ExternalJoinEnum(String name, int code){
        this.name = name;
        this.code = code;
    }
}
