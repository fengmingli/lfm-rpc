package com.lifengming.rpc.serialization.serialize.jdk;

import com.lifengming.rpc.common.annotation.MessageProtocolSupport;
import com.lifengming.rpc.common.constans.RpcConstant;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author lifengming
 * @date 2021.07.25
 */
@SuppressWarnings("unchecked")
@MessageProtocolSupport(RpcConstant.PROTOCOL_JAVA)
public class JavaSerializeMessageProtocol implements MessageProtocol {

    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(obj);
        return bout.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return (T) in.readObject();
    }
}
