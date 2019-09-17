package com.lfm.rpc.server;

/**
 * @author lifengming
 * @since 16.09.2019
 */
public class LfmRpcServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();

        LfmRpcServerProxy lfmRpcServerProxy = new LfmRpcServerProxy();
        lfmRpcServerProxy.publisher(helloService, 8080);
    }
}

