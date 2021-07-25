package com.lifengming.springboot.config;

import com.lifengming.rpc.common.constans.RpcConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lifengming
 * @date 2021.07.25
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "lfm.rpc")
public class RpcConfig {

    /**
     * 服务注册中心地址
     */
    private String registerAddress = "127.0.0.1:2181";

    /**
     * 服务暴露端口
     */
    private Integer serverPort = 9999;
    /**
     * 服务协议
     */
    private String protocol = RpcConstant.PROTOCOL_JAVA;
    /**
     * 负载均衡算法
     */
    private String loadBalance = RpcConstant.BALANCE_RANDOM;
    /**
     * 权重，默认为1
     */
    private Integer weight = 1;
}
