package com.lifengming.rpc.server.config;

import com.lifengming.rpc.core.util.NetUtils;
import com.lifengming.rpc.server.RpcLifecycle;
import com.lifengming.rpc.server.RpcServerInitialization;
import com.lifengming.rpc.server.netty.RpcServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lifengming
 * @since 2019.11.25
 */
@Configuration
public class RpcServerConfiguration {
    @Bean
    public RpcServer rpcServer() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostAddress = inetAddress != null ? inetAddress.getHostAddress() : null;
        return new RpcServer(hostAddress, NetUtils.getAvailablePort(hostAddress, 7777, 10000));
    }


    @Bean
    @ConditionalOnClass(RpcServerInitialization.class)
    public RpcLifecycle rpcLifecycle() {
        return new RpcLifecycle();
    }

    @Bean
    @ConditionalOnClass(RpcServer.class)
    public RpcServerInitialization rpcServerInitialization() {
        return new RpcServerInitialization();
    }
}
