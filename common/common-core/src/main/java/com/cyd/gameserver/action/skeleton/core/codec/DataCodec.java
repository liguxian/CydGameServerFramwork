package com.cyd.gameserver.action.skeleton.core.codec;

public interface DataCodec {

    byte[] encode(Object data);

    <T> T decode(byte[] data, Class<?> dataClass);

    /**
     * 编解码名
     *
     * @return 编解码名
     */
    default String codecName() {
        return this.getClass().getSimpleName();
    }
}
