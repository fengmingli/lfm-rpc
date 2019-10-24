package com.lifengming.bootstrap;

import com.lfm.rpc.core.model.ServiceAddress;
import com.lifengming.rpc.register.servicecenter.LocalServiceDiscoveryImpl;
import com.lifengming.rpc.register.servicecenter.LocalServiceRegistryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lifengming
 * @since 2019.10.24
 */
@Configuration
public class DiscoveryBootStrap {


    @Value("${spring.cloud.local.host}")
    private String host;

    @Value("${spring.cloud.local.port}")
    private String port;

    @Value("${spring.cloud.server.name}")
    private String serverName;

    /**
     * local服务发现
     *
     * @return
     */
    @Bean
    public LocalServiceDiscoveryImpl getDiscoveryLocal() {
        LocalServiceDiscoveryImpl localServiceDiscovery = new LocalServiceDiscoveryImpl();
        localServiceDiscovery.serviceDiscovery(serverName);
        return localServiceDiscovery;
    }

    /**
     * local服务注册
     *
     * @return
     */
    @Bean
    public LocalServiceRegistryImpl getRegistryConsul() {
        LocalServiceRegistryImpl localServiceRegistry = new LocalServiceRegistryImpl();
        ServiceAddress serviceAddress = new ServiceAddress();
        serviceAddress.setPort(Integer.valueOf(port));
        serviceAddress.setIp(host);
        localServiceRegistry.serviceRegister(serverName,serviceAddress);
        return localServiceRegistry;
    }
}
