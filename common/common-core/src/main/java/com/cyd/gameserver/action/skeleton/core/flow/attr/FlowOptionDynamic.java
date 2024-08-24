package com.cyd.gameserver.action.skeleton.core.flow.attr;

import java.util.Map;
import java.util.Objects;

/**
 * FlowContext的扩展属性
 */
public interface FlowOptionDynamic {

    /**
     * 获取所有动态属性
     *
     * @return
     */
    Map<FlowOption<?>, Object> getOptions();

    /**
     * 是否有某个动态属性
     *
     * @param flowOption
     * @return
     */
    default boolean hasOption(FlowOption<?> flowOption) {
        return getOptions().containsKey(flowOption);
    }

    /**
     * 获取某个动态属性
     *
     * @param flowOption
     * @param <T>
     * @return
     */
    default <T> T option(FlowOption<T> flowOption) {
        return (T) getOptions().get(flowOption);
    }

    /**
     * 在动态属性中设置值
     *
     * @param option option
     * @param value  设置的值，如果是 null 用于删除前一个
     * @param <T>    t
     * @return 前一个值
     */
    @SuppressWarnings("unchecked")
    default <T> T option(FlowOption<T> option, T value) {

        if (Objects.isNull(value)) {
            return (T) this.getOptions().remove(option);
        }

        return (T) this.getOptions().put(option, value);
    }
}
