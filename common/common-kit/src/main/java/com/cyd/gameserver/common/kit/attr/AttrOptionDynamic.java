package com.cyd.gameserver.common.kit.attr;

import java.util.Objects;
import java.util.function.Consumer;

public interface AttrOptionDynamic {

    //option集合
    AttrOptions getOptions();

    /**
     *获取选项值，若选项不在集合中，返回选项默认值
     */
    default <T> T option(AttrOption<T> option) {
        return getOptions().option(option);
    }

    /**
     * 获取选项值，若选项不在集合中，返回设定值
     */
    default <T> T optionValue(AttrOption<T> option, T value) {
        T val = option(option);
        if(Objects.isNull(val)) {
            val = value;
        }

        return val;
    }

    /**
     *设置一个具有设定值的新选项
     */
    default <T> AttrOptions option(AttrOption<T> option, T value) {
        return getOptions().option(option, value);
    }

    /**
     *若选项值存在（先取集合中设置的值，没有则取默认值），对其执行操作
     */
    default <T> void ifPresent(AttrOption<T> option, Consumer<T> consumer) {
        T val = option(option);
        if(Objects.nonNull(val)) {
            consumer.accept(val);
        }
    }
}
