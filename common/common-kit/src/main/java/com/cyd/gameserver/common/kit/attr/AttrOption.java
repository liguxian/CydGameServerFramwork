package com.cyd.gameserver.common.kit.attr;

public record AttrOption<T>(String name, T defaultValue) {

    /**
     * 初始化一个默认值为null的option
     * @param name
     * @return
     * @param <T>
     */
    public static <T> AttrOption<T> valueOf(String name) {
        return new AttrOption<>(name, null);
    }

    /**
     * 初始化一个给定默认值的option
     * @param name
     * @param defaultValue
     * @return
     * @param <T>
     */
    public static <T> AttrOption<T> valueOf(String name, T defaultValue) {
        return new AttrOption<>(name, defaultValue);
    }
}
