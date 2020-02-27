package com.lifengming.rpc.client.netty;

import com.lifengming.rpc.core.model.RpcResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.10.14
 */
public class ResponseFutureManager {

    private static volatile ResponseFutureManager responseFutureManager = null;

    private ResponseFutureManager() {
    }

    public static ResponseFutureManager getResponseFutureManagerInstance() {
        if (responseFutureManager == null) {
            synchronized (ResponseFutureManager.class) {
                if (responseFutureManager == null) {
                    responseFutureManager = new ResponseFutureManager();
                }
            }

        }
        return responseFutureManager;
    }

    private ConcurrentHashMap<String, RpcResponseFuture> rpcFutureMap = new ConcurrentHashMap<>();

    void futureDone(RpcResponse rpcResponse) {
        // Mark the responseFuture as done
        rpcFutureMap.remove(rpcResponse.getRequestId()).done(rpcResponse);
    }

    public void registerFuture(RpcResponseFuture rpcResponseFuture) {
        rpcFutureMap.put(rpcResponseFuture.getRequestId(), rpcResponseFuture);
    }
}
