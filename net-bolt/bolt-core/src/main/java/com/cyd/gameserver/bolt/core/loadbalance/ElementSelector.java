package com.cyd.gameserver.bolt.core.loadbalance;

import java.util.function.Supplier;

/**
 * 元素选择器
 */
public interface ElementSelector<T> extends Supplier<T> {

    T next();
}
