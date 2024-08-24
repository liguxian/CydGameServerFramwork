package com.cyd.gameserver.action.skeleton.core.codec;

import com.cyd.gameserver.common.ProtoKit;
import com.cyd.gameserver.common.Utils.BitUtil;
import com.cyd.gameserver.common.Utils.XXTEAUtil;
import com.cyd.gameserver.common.Utils.ZlibUtil;
import com.cyd.gameserver.common.consts.CommonConst;
import org.apache.commons.lang.ArrayUtils;

import java.util.Objects;

/**
 * 消息体的编解码，消息体的第一个字节用作一些信息的标识，如是否加密，压缩
 */
public class CydCodec implements DataCodec{
    @Override
    public byte[] encode(Object data) {

        byte[] bytes = ProtoKit.toBytes(data);

        //添加消息头
        byte header = 0; //byte，1-2-3-4-5-6-7-8 |1-是否压缩|2-是否加密
        //1.压缩
        if(bytes.length > 512) {
            header = BitUtil.SetBit(header, 0);
            ZlibUtil.compress(bytes);
        }
        //2.加密
        header = BitUtil.SetBit(header, 1);
        bytes = XXTEAUtil.Encrypt(bytes);

        return ArrayUtils.add(bytes, 0, header);
    }

    @Override
    public <T> T decode(byte[] data, Class<?> dataClass) {
        if(Objects.isNull(data)) {
            return (T) ProtoKit.parseProtoByte(CommonConst.emptyBytes, dataClass);
        }

        //消息头
        byte header = data[0];
        //是否压缩
        Boolean isCompress = BitUtil.GetBit(header, 0) == 1;
        //是否加密
        Boolean isEncrypt = BitUtil.GetBit(header, 1) == 1;

        data = ArrayUtils.remove(data, 0);

        if(isEncrypt) {
            //解密
            data = XXTEAUtil.Decrypt(data);
        }

        if(isCompress) {
            //解压
            data = ZlibUtil.decompress(data);
        }

        return (T) ProtoKit.parseProtoByte(data, dataClass);
    }
}
