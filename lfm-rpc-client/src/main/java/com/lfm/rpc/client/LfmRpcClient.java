package com.lfm.rpc.client;

/**
 * @author lifengming
 * @since 16.09.2019
 */
public class LfmRpcClient  {

    public static void main(String[] args) {
        LfmRpcProxy lfmRpcProxy = new LfmRpcProxy();
        HelloService helloService = lfmRpcProxy.clientProxy(HelloService.class, "localhost", 9999);
        System.out.println(helloService.sayHello("lfm"));
    }
}
