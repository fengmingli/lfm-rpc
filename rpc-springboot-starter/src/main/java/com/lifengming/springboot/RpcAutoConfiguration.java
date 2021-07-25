package com.lifengming.springboot;

import com.lifengming.rpc.client.LfmRpcClientProxy;
import com.lifengming.rpc.client.listener.ClientInitialization;
import com.lifengming.rpc.client.netty.NettyRpcClient;
import com.lifengming.rpc.common.annotation.LoadBalanceSupport;
import com.lifengming.rpc.common.annotation.MessageProtocolSupport;
import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.register.ServerRegister;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;
import com.lifengming.rpc.register.zookeeper.ZookeeperServerDiscovery;
import com.lifengming.rpc.register.zookeeper.ZookeeperServerRegister;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;
import com.lifengming.rpc.server.listener.ServerInitialization;
import com.lifengming.rpc.server.netty.NettyRpcServer;
import com.lifengming.rpc.server.netty.RequestHandler;
import com.lifengming.rpc.server.netty.RpcServer;
import com.lifengming.springboot.config.RpcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author lifengming
 * @date 2021.07.24
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcAutoConfiguration {

    @Bean
    public RpcConfig rpcConfig() {
        return new RpcConfig();
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setThreadNamePrefix("rpc-service-");
        return threadPoolTaskExecutor;
    }

    @Bean
    public ServerRegister serverRegister(
            @Autowired RpcConfig rpcConfig) {

        return new ZookeeperServerRegister(
                rpcConfig.getRegisterAddress(),
                rpcConfig.getServerPort(),
                rpcConfig.getProtocol(),
                rpcConfig.getWeight());
    }

    @Bean
    public RequestHandler requestHandler(
            @Autowired ServerRegister serverRegister,
            @Autowired RpcConfig rpcConfig) {

        return new RequestHandler(getMessageProtocol(rpcConfig.getProtocol()), serverRegister);
    }


    @Bean
    public RpcServer rpcServer(
            @Autowired RequestHandler requestHandler,
            @Autowired RpcConfig rpcConfig) {

        return new NettyRpcServer(rpcConfig.getServerPort(), rpcConfig.getProtocol(), requestHandler);
    }


    @Bean
    public LfmRpcClientProxy proxyFactory(
            @Autowired RpcConfig rpcConfig) {

        LfmRpcClientProxy clientProxyFactory = new LfmRpcClientProxy();
        // 设置服务发现
        clientProxyFactory.setServerDiscovery(new ZookeeperServerDiscovery(rpcConfig.getRegisterAddress()));

        // 设置支持的协议
        Map<String, MessageProtocol> supportMessageProtocols = buildSupportMessageProtocols();
        clientProxyFactory.setSupportMessageProtocols(supportMessageProtocols);

        // 设置负载均衡算法
        LoadBalancer<Service> loadBalance = getLoadBalance(rpcConfig.getLoadBalance());
        clientProxyFactory.setLoadBalancer(loadBalance);

        // 设置网络层实现
        clientProxyFactory.setNetworkClient(new NettyRpcClient());

        return clientProxyFactory;
    }

    @Bean
    public ClientInitialization rpcClientProcessor(
            @Autowired LfmRpcClientProxy clientProxyFactory) {

        return new ClientInitialization(clientProxyFactory);
    }

    @Bean
    public ServerInitialization rpcServerProcessor(
            @Autowired ServerRegister serverRegister,
            @Autowired RpcServer rpcServer,
            @Autowired ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new ServerInitialization(rpcServer, serverRegister, threadPoolTaskExecutor);
    }


    private Map<String, MessageProtocol> buildSupportMessageProtocols() {
        Map<String, MessageProtocol> supportMessageProtocols = new HashMap<>(8);
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        for (MessageProtocol messageProtocol : loader) {
            MessageProtocolSupport ano = messageProtocol.getClass().getAnnotation(MessageProtocolSupport.class);
            Assert.notNull(ano, "message protocol name can not be empty!");
            supportMessageProtocols.put(ano.value(), messageProtocol);
        }
        return supportMessageProtocols;
    }


    private MessageProtocol getMessageProtocol(String name) {
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        for (MessageProtocol messageProtocol : loader) {
            MessageProtocolSupport messageProtocolSupport = messageProtocol.getClass().getAnnotation(MessageProtocolSupport.class);
            Assert.notNull(messageProtocolSupport, "message protocol name can not be empty!");
            if (name.equals(messageProtocolSupport.value())) {
                return messageProtocol;
            }
        }
        throw new RpcException("invalid message protocol config!");
    }

    /**
     * 使用spi匹配符合配置的负载均衡算法
     *
     * @param name 服务名
     * @return LoadBalance
     */
    @SuppressWarnings("all")
    private static LoadBalancer<Service> getLoadBalance(String name) {
        ServiceLoader<LoadBalancer> loader = ServiceLoader.load(LoadBalancer.class);
        for (LoadBalancer<Service> loadBalance : loader) {
            LoadBalanceSupport loadBalanceSupport = loadBalance.getClass().getAnnotation(LoadBalanceSupport.class);
            Assert.notNull(loadBalanceSupport, "load balance name can not be empty!");
            if (name.equals(loadBalanceSupport.value())) {
                return loadBalance;
            }
        }
        throw new RpcException("invalid load balance config");
    }
}
