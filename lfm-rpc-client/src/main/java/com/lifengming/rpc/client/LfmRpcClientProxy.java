package com.lifengming.rpc.client;

import com.lifengming.rpc.client.netty.ChannelManager;
import com.lifengming.rpc.client.netty.ResponseFutureManager;
import com.lifengming.rpc.client.netty.RpcResponseFuture;
import com.lifengming.rpc.core.exception.LfmException;
import com.lifengming.rpc.core.model.RpcRequest;
import com.lifengming.rpc.core.model.RpcResponse;
import com.lifengming.rpc.core.util.ApplicationHelper;
import com.lifengming.rpc.register.servicecenter.ServiceDiscovery;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 负责调用的过程：
 * 1.封装请求参数为RpcRequest
 * 2.通过ip+port获取服务地址
 * 3.通过地址拿到netty连接通道，使用netty将RpcRequest发送到地址
 * 4.返回response
 *
 * @author lifengming
 * @since 16.09.2019
 */
@Slf4j
public class LfmRpcClientProxy {

    /**
     * 通过代理去调用
     * 动态代理：java原生/cglib/javassit/asm
     * <p>
     *
     * @param interfaceClass HelloService.class
     * @return 代理实例
     */
    Object clientProxy(Class<?> interfaceClass) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, (proxy, method, args) -> {
            log.info("开始代理发现服务，发送请求过程");
            String targetServiceName = interfaceClass.getName();

            //creat request
            RpcRequest request = RpcRequest.builder().requestId(generateRequestId(targetServiceName))
                    .interfaceName(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .parameters(args)
                    .parameterTypes(method.getParameterTypes()).build();

            // Get service address
            InetSocketAddress serviceAddress = getServiceAddress(targetServiceName);

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
        });
    }


    /**
     * 根据服务名，从注册中心获取address
     **/
    private InetSocketAddress getServiceAddress(String targetServiceName) {
        String serviceAddress;
        ServiceDiscovery serviceDiscovery = (ServiceDiscovery) ApplicationHelper.getBean(ServiceDiscovery.class);
        serviceAddress = serviceDiscovery.serviceDiscovery(targetServiceName);
        log.debug("Get address: {} for service: {}", serviceAddress, targetServiceName);

        if (StringUtils.isEmpty(serviceAddress)) {
            throw new RuntimeException(String.format("Address of target service %s is empty", targetServiceName));
        }

        String[] array = StringUtils.split(serviceAddress, ":");
        assert array != null;
        String host = array[0];
        int port = Integer.parseInt(array[1]);

        return new InetSocketAddress(host, port);
    }

    /**
     * 向netty通道发送request
     **/
    private RpcResponse sendRequest(Channel channel, RpcRequest request) {
        CountDownLatch latch = new CountDownLatch(1);
        RpcResponseFuture rpcResponseFuture = new RpcResponseFuture(request.getRequestId());
        ResponseFutureManager.getResponseFutureManagerInstance().registerFuture(rpcResponseFuture);
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> latch.countDown());

        try {
            log.info("向netty通道发送request请求完成");
            latch.await();
        } catch (InterruptedException e) {
            log.error("向netty通道发送request出现异常:{}", e.getMessage());
        }

        try {
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
