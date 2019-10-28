package com.lifengming.rpc.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lifengming
 * @since 2019.10.28
 */
@Configuration
public class RpcClientBootstrap {


    /**
     * 将使用了@ZrpcService注解的接口，在注入到spring容器里时，代理为自定义FactoryBean
     *
     * @return
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
