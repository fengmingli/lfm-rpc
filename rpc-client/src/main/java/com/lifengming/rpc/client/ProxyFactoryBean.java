package com.lifengming.rpc.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author lifengming
 * @since 2019.10.23
 */
@Data
@Slf4j
public class ProxyFactoryBean implements FactoryBean<Object> {

    private Class<?> type;
    private LfmRpcClientProxy lfmRpcClientProxy;

    @Override
    public Object getObject() throws Exception {
        return lfmRpcClientProxy.clientProxy(type);
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
