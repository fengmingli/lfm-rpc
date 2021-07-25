package com.lifengming.rpc.register.zookeeper;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.StandardCharsets;

/**
 * zk序列化器
 *
 * @author lifengming
 * @date 2021.07.24
 */
public class MyZookeeperSerializer implements ZkSerializer {

    /**
     * 反序列化
     * @param bytes byte[]
     * @return Object
     * @throws ZkMarshallingError  序列化异常
     */
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 序列化
     * @param object 对象
     * @return byte[]
     * @throws ZkMarshallingError 序列化异常
     */
    @Override
    public byte[] serialize(Object object) throws ZkMarshallingError {
        return String.valueOf(object).getBytes(StandardCharsets.UTF_8);
    }
}
