package com.lfm.rpc.client.netty;

import com.lfm.rpc.core.model.RpcResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.10.14
 */
class ResponseFutureManager {

    private static volatile ResponseFutureManager responseFutureManager = null;

    private ResponseFutureManager() {
    }

    static ResponseFutureManager getInstance() {
        if (responseFutureManager == null) {
            synchronized (ResponseFutureManager.class) {
                if (responseFutureManager == null) {
                    responseFutureManager = new ResponseFutureManager();
                }
            }

        }
        return responseFutureManager;
    }

    private ConcurrentHashMap<String, RpcResponseFuture> rpcFutureMap = new ConcurrentHashMap<String, RpcResponseFuture>();

    void futureDone(RpcResponse rpcResponse) {
        // Mark the responseFuture as done
        rpcFutureMap.remove(rpcResponse.getRequestId()).done(rpcResponse);
    }

    public void registerFuture(RpcResponseFuture rpcResponseFuture) {
        rpcFutureMap.put(rpcResponseFuture.getRequestId(), rpcResponseFuture);
    }
}
