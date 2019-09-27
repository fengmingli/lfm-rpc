package com.lfm.rpc.core.serialize;

/**
 * @author lifengming
 * @since 2019.09.17
 */
public abstract class Serializer {
    public abstract <T> byte[] serialize(T obj);
    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
