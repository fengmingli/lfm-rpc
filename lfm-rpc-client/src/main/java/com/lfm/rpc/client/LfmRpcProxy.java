package com.lfm.rpc.client;

import java.lang.reflect.Proxy;

/**
 * @author lifengming
 * @since 16.09.2019
 */
public class LfmRpcProxy {

    /**
     * 通过代理去调用
     * 动态代理：java原生/cglib/javassit/asm
     *
     * @param interfacCls
     * @param host
     * @param port
     * @param <T>
     * @return
     */
    public <T> T clientProxy(Class<T> interfacCls, String host, int port) {
        return (T) Proxy.newProxyInstance(interfacCls.getClassLoader(), new Class<?>[]{interfacCls}, new RemoteInvocationHandler(host,port));
    }
}
