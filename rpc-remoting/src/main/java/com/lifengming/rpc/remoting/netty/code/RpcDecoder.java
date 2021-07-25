package com.lifengming.rpc.remoting.netty.code;

import com.lifengming.rpc.serialization.serialize.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author lifengming
 * @since 2019.09.29
 */
@AllArgsConstructor
public class RpcDecoder extends ByteToMessageDecoder {

    public static final int MAX_SIZE = 4;
    private final Class<?> genericClass;
    private final MessageProtocol messageProtocol;

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < MAX_SIZE) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(messageProtocol.deserialize(data, genericClass));
    }
}
