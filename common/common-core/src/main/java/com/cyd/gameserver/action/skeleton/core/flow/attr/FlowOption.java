package com.cyd.gameserver.action.skeleton.core.flow.attr;

import java.util.Objects;

/**
 * FlowContext的扩展属性
 * @param name
 * @param <T>
 */
public record FlowOption<T>(String name) {

    public static <T> T valueOf(String name){
        return (T)new FlowOption<>(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FlowOption<?> that)) {
            return false;
        }

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
