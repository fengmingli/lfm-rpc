package com.lifengming.rpc.register.servicecenter;

import com.lifengming.rpc.core.model.ServiceAddress;

/**
 * 服务注册接口
 * <p></>
 *
 * @author lifengming
 * @since 2019.10.23
 */
public interface ServiceRegistry {
    /**
     * 通过服务地址与服务名进行注册
     *
     * @param serviceName    服务名字
     * @param serviceAddress 服务地址
     */
    void serviceRegister(String serviceName, ServiceAddress serviceAddress);
}
