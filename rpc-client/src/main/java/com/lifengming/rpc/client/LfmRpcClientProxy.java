package com.lifengming.rpc.client;


import com.lifengming.rpc.client.netty.NetworkClient;
import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.register.ServerDiscovery;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;
import com.lifengming.rpc.register.localcache.ServiceLocalCache;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 客户端代理工厂：用于创建远程服务代理类
 *
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
@Getter
@Setter
public class LfmRpcClientProxy {
    private ServerDiscovery serverDiscovery;
    private LoadBalancer<Service> loadBalancer;
    private Map<String, MessageProtocol> supportMessageProtocols;
    private NetworkClient networkClient;

    /**
     * 通过代理去调用
     * 动态代理：java原生/cglib/javassit/asm
     * <p>
     *
     * @param interfaceClass HelloService.class
     * @return 代理实例
     */
    public Object clientProxy(final Class<?> interfaceClass) {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, (proxy, method, args) -> {
            log.info("开始代理发现服务，发送请求过程");
            String targetServiceName = interfaceClass.getName();

            // 1、获取服务信息
            List<Service> serviceList = getServiceList(targetServiceName);
            // 1.1 服务负载
            Service service = loadBalancer.next(serviceList);

            //2、构建 RpcRequest 对象
            RpcRequest request = RpcRequest.builder()
                    .requestId(generateRequestId(targetServiceName))
                    .serviceName(service.getName())
                    .methodName(method.getName())
                    .parameters(args)
                    .parameterTypes(method.getParameterTypes()).build();

            // 3、协议层
            MessageProtocol messageProtocol = supportMessageProtocols.get(service.getProtocol());

            //4、发送请求
            RpcResponse rpcResponse = networkClient.sendRequest(request, service, messageProtocol);

            if (rpcResponse == null) {
                throw new RpcException("response is null");
            }

            if (rpcResponse.hasException()) {
                throw new RpcException(rpcResponse.getException());
            } else {
                log.info("{}:调用收到的响应为：{}", rpcResponse.getRequestId(), rpcResponse.getResult());
                return rpcResponse.getResult();
            }
        });
    }

    /**
     * 根据服务名获取可用的服务地址列表
     * @param serviceName 服务名
     * @return 服务列表
     */
    private List<Service> getServiceList(String serviceName) {
        List<Service> services;
        synchronized (serviceName) {
            //1、缓存中是否存在
            if (ServiceLocalCache.isEmpty(serviceName)) {
                //缓存不存在，直接到远程注册中心获取
                services = serverDiscovery.getServiceList(serviceName);
                if (services == null || services.size() == 0) {
                    throw new RpcException("No provider available!");
                }
                //放入缓存
                ServiceLocalCache.put(serviceName, services);
            } else {
                //2、缓存存在，直接获取
                services = ServiceLocalCache.getServicesByServiceName(serviceName);
            }
        }
        return services;
    }

    private String generateRequestId(String targetServiceName) {
        return targetServiceName + "-" + UUID.randomUUID();
    }
}
