package com.lifengming.rpc.register.servicecenter;

import com.lfm.rpc.core.model.ServiceAddress;

import static com.lifengming.rpc.register.servicecenter.LocalHost.getLocalHost;

/**
 * @author lifengming
 * @since 2019.10.23
 */
public class LocalServiceRegistryImpl implements ServiceRegistry {

    @Override
    public void serviceRegister(String serviceName, ServiceAddress serviceAddress) {
        getLocalHost().serviceRegister(serviceName, serviceAddress);
    }
}
