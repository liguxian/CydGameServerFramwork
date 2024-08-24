package com.cyd.gameserver.common.kit;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class PresentKit {

    /**
     * 如果obj不存在，则执行操作，否则不执行
     * @param obj
     * @param runnable
     */
    public void ifNull(Object obj, Runnable runnable) {
        if (Objects.isNull(obj)) {
            runnable.run();
        }
    }
}
