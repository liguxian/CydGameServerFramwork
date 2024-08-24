package com.cyd.gameserver.bolt.core.loadbalance;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 元素选择器
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RandomElementSelector<T> implements ElementSelector<T> {

    final List<T> elements;

    final int size;

    public RandomElementSelector(List<T> elements){
        this.elements = elements;
        this.size = elements.size();
    }

    @Override
    public T next() {
        T element = null;
        if(size > 1){

            ThreadLocalRandom random = ThreadLocalRandom.current();
            element = elements.get(random.nextInt(size));

            if(Objects.isNull(element)) {
                element = elements.get(0);
            }

        } else if(size == 1) {
            element = elements.get(0);
        }
        return element;
    }

    @Override
    public T get() {
        T next = next();

        if(Objects.isNull(next)) {
            throw new NullPointerException("RandomSelector next is null!");
        }
        return next;
    }
}
