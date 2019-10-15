package com.lfm.rpc.client;

import com.lfm.rpc.client.netty.ChannelManager;
import com.lfm.rpc.client.netty.ResponseFutureManager;
import com.lfm.rpc.client.netty.RpcResponseFuture;
import com.lfm.rpc.core.exception.LfmException;
import com.lfm.rpc.core.model.RpcRequest;
import com.lfm.rpc.core.model.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author lifengming
 * @since 16.09.2019
 */
@AllArgsConstructor
@Slf4j
public class RemoteInvocationHandler implements InvocationHandler {

    String host;
    int port;
    Class<?> classType;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("开始代理发现服务，发送请求过程");
        String targetServiceName = classType.getName();

        //creat request
        RpcRequest request = RpcRequest.builder().requestId(generateRequestId(targetServiceName))
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes()).build();

        // Get service address
        InetSocketAddress serviceAddress = new InetSocketAddress(host, port);

        // Get channel by service address
        Channel channel = ChannelManager.getChannelManagerInstance().getChannel(serviceAddress);
        if (null == channel) {
            throw new LfmException("Can't get channel for address" + serviceAddress);
        }

        //send request
        RpcResponse response = sendRequest(channel, request);
        if (response == null) {
            throw new LfmException("response is null");
        }

        if (response.hasException()) {
            throw new LfmException(response.getException());
        } else {
            log.info("{}:调用收到的响应为：{}", response.getRequestId(), response.getResult());
            return response.getResult();
        }
    }

    /**
     * 向netty通道发送request
     **/
    private RpcResponse sendRequest(Channel channel, RpcRequest request) {
        CountDownLatch latch = new CountDownLatch(1);
        RpcResponseFuture rpcResponseFuture = new RpcResponseFuture(request.getRequestId());
        ResponseFutureManager.getResponseFutureManagerInstance().registerFuture(rpcResponseFuture);

        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            latch.countDown();
        });

        try {
            log.info("向netty通道发送request出现完成");
            latch.await();
        } catch (InterruptedException e) {
            log.error("向netty通道发送request出现异常:{}", e.getMessage());
        }

        try {
            // TODO: make timeout configurable
            // return response
            return rpcResponseFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Exception:", e);
            return null;
        }
    }

    private String generateRequestId(String targetServiceName) {
        return targetServiceName + "-" + UUID.randomUUID().toString();
    }
}
