package com.lifengming.rpc.client.netty;

import com.lifengming.rpc.core.model.RpcResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.*;

/**
 * @author lifengming
 * @since 2019.10.14
 */

@Data
@RequiredArgsConstructor
public class RpcResponseFuture implements Future<Object> {
    @NonNull
    private String requestId;

    private RpcResponse rpcResponse;

    CountDownLatch latch = new CountDownLatch(1);

    void done(RpcResponse response) {
        this.rpcResponse = response;
        latch.countDown();
    }

    @Override
    public RpcResponse get() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit timeUnit) throws TimeoutException {
        try {
            if (!latch.await(timeout, timeUnit)) {
                throw new TimeoutException("RPC Request timeout!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
