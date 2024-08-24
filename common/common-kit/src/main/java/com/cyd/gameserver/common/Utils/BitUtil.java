package com.cyd.gameserver.common.Utils;

public class BitUtil {

    /**
     * 获取取第index位
     *
     * @param b
     * @param index
     * @return
     */
    public static int GetBit(byte b, int index) {
        return (b & (1 << index)) > 0 ? 1 : 0;
    }

    /**
     * 将第index位设为1
     *
     * @param b
     * @param index
     * @return
     */
    public static byte SetBit(byte b, int index) {
        return (byte) (b | (1 << index));
    }

    /**
     * 将第index位设为0
     *
     * @param b
     * @param index
     * @return
     */
    public static byte ClearBit(byte b, int index) {
        return (byte) (b & (Byte.MAX_VALUE - (1 << index)));
    }

    /**
     * 将第index位取反
     *
     * @param b
     * @param index
     * @return
     */
    public static byte ReverseBit(byte b, int index) {
        return (byte) (b ^ (byte) (1 << index));
    }
}
