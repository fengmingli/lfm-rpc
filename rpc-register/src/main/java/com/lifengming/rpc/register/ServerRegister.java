package com.lifengming.rpc.register;

import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.ServiceObject;

/**
 * 服务注册器，定义服务注册规范
 *
 * @author lifengming
 * @date 2021.07.24
 */
public interface ServerRegister {

    /**
     * 服务注册
     * @param serviceObject 服务持有对象
     * @throws RpcException  注册异常
     */
    void serviceRegister(ServiceObject serviceObject) throws RpcException;

    /**
     *服务发现
     *
     * @param serviceName 服务名
     * @return ServiceObject
     * @throws RpcException /、服务发现异常
     */
    ServiceObject getServiceObjByServiceName(String serviceName) throws RpcException;
}
