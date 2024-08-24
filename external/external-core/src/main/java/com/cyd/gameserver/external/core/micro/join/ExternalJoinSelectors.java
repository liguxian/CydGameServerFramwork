package com.cyd.gameserver.external.core.micro.join;

import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import lombok.experimental.UtilityClass;

import java.util.EnumMap;

@UtilityClass
public class ExternalJoinSelectors {

    final EnumMap<ExternalJoinEnum, ExternalJoinSelector> map = new EnumMap<>(ExternalJoinEnum.class);

    public void putIfAbsent(ExternalJoinSelector selector) {
        putIfAbsent(selector.getExternalJoinEnum(), selector);
    }

    public void putIfAbsent(ExternalJoinEnum externalJoinEnum, ExternalJoinSelector externalJoinSelector) {
        map.putIfAbsent(externalJoinEnum, externalJoinSelector);
    }

    public ExternalJoinSelector getExternalJoinSelector(ExternalJoinEnum externalJoinEnum) {
        if(!map.containsKey(externalJoinEnum)) {
           throw new RuntimeException("没有对应的连接方式 " + externalJoinEnum);
        }

        return map.get(externalJoinEnum);
    }

}
