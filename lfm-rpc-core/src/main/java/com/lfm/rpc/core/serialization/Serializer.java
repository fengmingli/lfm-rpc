package com.lfm.rpc.core.serialization;

/**
 * <p>定义一个序列化抽象接口<p/>
 *
 * @author lifengming
 * @since 2019.09.17
 */
public interface Serializer  {
    /**
     * 把对象转换成byte数组
     * @param obj 对象
     * @return Object
     * */
    public abstract <T> byte[] serialize(T obj);

    /**
     * 把数组与类 转换成一个对象
     * @param bytes byte数组
     * @param clazz 类
     * @param <T> 对象
     * @return Object
     */
    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
