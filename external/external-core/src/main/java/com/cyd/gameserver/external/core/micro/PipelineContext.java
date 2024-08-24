package com.cyd.gameserver.external.core.micro;

import java.util.Objects;

/**
 * pipeline上下文
 */
public interface PipelineContext {

    default void addFirst(Object handler) {
        Objects.requireNonNull(handler);
        String name = handler.getClass().getSimpleName();
        addFirst(name, handler);
    }

    void addFirst(String name, Object handler);

    default void addLast(Object handler) {
        Objects.requireNonNull(handler);
        String name = handler.getClass().getSimpleName();
        addLast(name, handler);
    }

    void addLast(String name, Object handler);

    void remove(String name);
}
