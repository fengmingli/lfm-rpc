package com.lfm.rpc.client;

import java.lang.reflect.Proxy;

/**
 * 负责调用的带路过程：
 * 1.封装请求参数为RpcRequest
 * 2.通过ip+port获取服务地址
 * 3.通过地址拿到netty连接通道，使用netty将RpcRequest发送到地址
 * 4.返回response
 *
 * @author lifengming
 * @since 16.09.2019
 */
public class LfmRpcProxy {

    /**
     * 通过代理去调用
     * 动态代理：java原生/cglib/javassit/asm
     *
     * @param classType
     * @param host
     * @param port
     * @param <T>
     * @return
     */
    public <T> T clientProxy(Class<T> classType, String host, int port) {
        return (T) Proxy.newProxyInstance(classType.getClassLoader(), new Class<?>[]{classType}, new RemoteInvocationHandler(host, port, classType));
    }
}
