package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.codec.CydCodec;
import com.cyd.gameserver.action.skeleton.core.codec.DataCodec;
import com.cyd.gameserver.common.ProtoKit;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataCodecKit {

    /** 业务数据的编解码器  在GlobalSetting中设置 */
    @Getter
    DataCodec codec = new CydCodec();

    /**
     * 将业务参数编码成字节数组
     *
     * @param data 业务参数 (指的是请求端的请求参数)
     * @return bytes
     */
    public byte[] encode(Object data){
        return codec.encode(data);
    }

    /**
     * 将字节数组解码成对象
     *
     * @param data       业务参数 (指的是请求端的请求参数)
     * @param paramClazz clazz
     * @param <T>        t
     * @return 业务参数
     */
    public <T> T decode(byte[] data, Class<T> paramClazz) {
        return codec.decode(data, paramClazz);
    }

    void setDataCodec(DataCodec codec){
        DataCodecKit.codec = codec;
    }
}
