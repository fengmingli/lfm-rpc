package com.lifengming.rpc.register;

import com.lifengming.rpc.register.servicecenter.consulservice.ConsulServiceDiscoveryImpl;
import com.lifengming.rpc.register.servicecenter.consulservice.ConsulServiceRegistryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lifengming
 * @since 2019.10.28
 */
@Configuration
public class RpcDiscoveryBootstrap {

    @Value("${spring.cloud.consul.host}")
    private String consulHost;

    @Value("${spring.cloud.consul.port}")
    private String consulPort;

    /**
     * consul服务发现
     * @return
     */
    @Bean
    public ConsulServiceDiscoveryImpl getDiscoveryConsul() {
        ConsulServiceDiscoveryImpl consulServiceDiscovery = new ConsulServiceDiscoveryImpl(consulHost+":"+consulPort);
        consulServiceDiscovery.setDiscoveryAddress(consulHost+":"+consulPort);
        return consulServiceDiscovery;
    }

    /**
     * consul服务注册
     * @return
     */
    @Bean
    public ConsulServiceRegistryImpl getRegistryConsul() {
        return new ConsulServiceRegistryImpl(consulHost + ":" + consulPort);
    }
}
