package com.lifengming.rpc.client.config;

import com.lifengming.rpc.client.LfmRpcClientProxy;
import com.lifengming.rpc.client.ServiceBeanDefinitionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lifengming
 * @since 2019.11.25
 */
@Configuration
public class RpcClientConfiguration {

    /**
     * 将使用了@ZrpcService注解的接口，在注入到spring容器里时，代理为自定义FactoryBean
     *
     * @return ServiceBeanDefinitionHandler
     */
    @Bean
    public ServiceBeanDefinitionHandler serviceBeanDefinitionHandler() {
        return new ServiceBeanDefinitionHandler(lfmRpcClientProxy());
    }

    /**
     * 初始话RPCclient
     * @return LfmRpcClientProxy
     */
    @Bean
    public LfmRpcClientProxy lfmRpcClientProxy() {
        return new LfmRpcClientProxy();
    }

}
