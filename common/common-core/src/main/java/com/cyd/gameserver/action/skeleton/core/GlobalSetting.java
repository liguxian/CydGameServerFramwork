package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.codec.DataCodec;

/**
 * 全局设置
 */
public class GlobalSetting {

    public static void setDataCodec(DataCodec codec) {
        DataCodecKit.setDataCodec(codec);
    }
}
