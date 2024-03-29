package com.lifengming.sample.server.service;

import com.lifengming.rpc.common.annotation.RpcService;
import com.lifengming.rpcsample.api.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author lifengming
 * @since 2019.10.16
 */

@Service
@RpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String getDocker() {
        return "基于Netty的Rpc框架测试成功！xxx！！";
    }
}
