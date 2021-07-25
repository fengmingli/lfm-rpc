package com.lifengming.rpc.serialization.serialize;

/**
 * <p>定义一个序列化上层抽象接口<p/>
 *
 * @author lifengming
 * @since 2019.09.17
 */
public interface MessageProtocol {
    /**
     * 把对象转换成byte数组
     * @param obj 对象
     * @throws Exception 序列化异常
     * @return Object
     * */
    public abstract <T> byte[] serialize(T obj) throws Exception;

    /**
     * 把数组与类 转换成一个对象
     * @param bytes byte数组
     * @param clazz 类
     * @param <T> 对象
     * @throws Exception 序列化异常
     * @return Object
     */
    public abstract <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}
