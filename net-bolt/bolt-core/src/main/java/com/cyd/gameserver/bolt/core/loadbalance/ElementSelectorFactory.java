package com.cyd.gameserver.bolt.core.loadbalance;

import java.util.List;

/**
 * 元素选择器工厂
 */
public interface ElementSelectorFactory<T> {

    ElementSelector<T> createElementSelector(List<T> elements);
}
