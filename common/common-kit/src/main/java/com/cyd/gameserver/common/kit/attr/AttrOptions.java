package com.cyd.gameserver.common.kit.attr;

import org.jctools.maps.NonBlockingHashMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AttrOptions {

    final Map<AttrOption<?>, Object> options = new NonBlockingHashMap<>();

    /**
     *获取选项值，若选项不存在，则返回默认值
     */
    public <T> T option(AttrOption<T> option){
        Object value = options.get(option);
        if(Objects.isNull(value)) {
            value = option.defaultValue();
        }

        return (T) value;
    }

    /**
     * 设置一个具有特定值的新选项,若设置的值为null，则为移除选项
     */
    public <T> AttrOptions option(AttrOption<T> option, T value) {
        if(Objects.isNull(value)) {
            options.remove(option);
            return this;
        }

        options.put(option, value);
        return this;
    }
}
