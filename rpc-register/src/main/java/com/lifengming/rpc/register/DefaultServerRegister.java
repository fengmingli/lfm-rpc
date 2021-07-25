package com.lifengming.rpc.register;

import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.ServiceObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认服务注册
 *
 * @author lifengming
 * @date 2021.07.24
 */
public abstract class DefaultServerRegister implements ServerRegister {
    private final Map<String, ServiceObject> serviceMap = new HashMap<>();

    protected String protocol;

    protected Integer port;
    /**
     * 权重
     */
    protected Integer weight;

    @Override
    public void serviceRegister(ServiceObject serviceObject) throws RpcException {
        if (serviceObject == null) {
            throw new RpcException("service register serviceObject cannot be empty");
        }
        serviceMap.put(serviceObject.getServiceName(), serviceObject);
    }

    @Override
    public ServiceObject getServiceObjByServiceName(String serviceName) throws RpcException {
        return serviceMap.get(serviceName);
    }
}
