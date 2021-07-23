package com.lifengming.rpcsample.api;


import com.lifengming.rpc.common.annotation.RpcService;

/**
 * @author lifengming
 * @since 2019.10.16
 */
@RpcService(interfaceClass = HelloService.class)
public interface HelloService {
    /**
     * 测试接口
     * @return
     */
    String getDocker();
}
