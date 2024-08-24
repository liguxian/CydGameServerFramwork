package com.cyd.gameserver.external.core.netty.handler.codec;

import com.cyd.gameserver.action.skeleton.core.DataCodecKit;
import com.cyd.gameserver.external.core.message.ExternalMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Tcp消息编解码
 */
public class TcpExternalCodec extends MessageToMessageCodec<ByteBuf, ExternalMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ExternalMessage externalMessage, List<Object> out) {

        /*
         *【游戏对外服】发送消息给【游戏客户端】
         * 编码器 - ExternalMessage ---> 字节数组，将消息发送到请求端（客户端）
         */
        byte[] bytes = DataCodecKit.encode(externalMessage);

        /*
         * 使用默认 buffer 。如果没有做任何配置，通常默认实现为池化的 direct （直接内存，也称为堆外内存）
         * 优点：使用的系统内存，读写效率高（少一次拷贝），且不受 GC 影响
         * 缺点：分配效率低
         */
        ByteBuf buffer = channelHandlerContext.alloc().buffer(bytes.length + 4);

        //消息=消息长度+消息体
        // 消息长度
        buffer.writeInt(bytes.length);
        // 消息
        buffer.writeBytes(bytes);

        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> out) {
        while (true) {
            ////消息=消息长度+消息体，先读取消息长度，再读取消息长度的消息体

            if(buffer.readableBytes() >= 4) {
                //标记初始读游标位置
                buffer.markReaderIndex();
                // 读取消息长度
                int length = buffer.readInt();
                //数据包还没到齐
                if (buffer.readableBytes() < length) {
                    buffer.resetReaderIndex();
                    return;
                }

                //读数据部分
                byte[] msgBytes = new byte[length];
                buffer.readBytes(msgBytes);

                ExternalMessage message = DataCodecKit.decode(msgBytes, ExternalMessage.class);
                //解析出消息对象，继续往下面的handler传递.
                out.add(message);
            } else {
                return;
            }
        }
    }
}
