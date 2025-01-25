package com.kneeg.myRPCVersion4.codec;

import com.kneeg.myRPCVersion4.common.RPCRequest;
import com.kneeg.myRPCVersion4.common.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * 依次按照自定义的消息格式写入，传入的数据为request或者response
 * 需要持有一个serialize器，负责将传入的对象序列化成字节数组
 */
@AllArgsConstructor
public class MyEncode extends MessageToByteEncoder {
    private Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println(msg.getClass());
        // 1. 写入消息类型（2字节）
        if (msg instanceof RPCRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
        } else if (msg instanceof RPCResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
        }

        // 2. 写入序列化类型（2字节）
        out.writeShort(serializer.getType());

        // 3. 序列化对象为字节数组
        byte[] serialize = serializer.serializer(msg);

        // 4. 写入数据长度（4字节）
        out.writeInt(serialize.length);

        // 5. 写入数据内容（N字节）
        out.writeBytes(serialize);
    }
}
