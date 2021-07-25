package com.lifengming.rpc.remoting.netty.code;

import com.lifengming.rpc.serialization.serialize.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * @author lifengming
 * @since 2019.09.29
 */
@AllArgsConstructor
public class RpcEncoder extends MessageToByteEncoder<Object> {

    private final Class<?> genericClass;
    private final MessageProtocol messageProtocol;

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            //序列化
            byte[] data = messageProtocol.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
