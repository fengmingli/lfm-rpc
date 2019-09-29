package com.lfm.rpc.client;

import com.lfm.rpc.core.model.RpcRequest;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author lifengming
 * @since 16.09.2019
 */
@AllArgsConstructor
public class RemoteInvocationHandler implements InvocationHandler {

    String host;
    int port;

    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameters(args);
        LfmRpcNetTransport lfmRpcNetTransport = new LfmRpcNetTransport(host, port);

        return lfmRpcNetTransport.sendRequest(rpcRequest);
    }
}
