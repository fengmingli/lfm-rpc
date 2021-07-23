package com.lifengming.rpc.remoting.netty;

import com.lifengming.rpc.common.model.RpcResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    public RpcResponse get() throws InterruptedException, ExecutionException {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rpcResponse;
    }

    @Override
    public RpcResponse get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            if (!latch.await(timeout, unit)) {
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
