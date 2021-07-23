package com.lifengming;

import com.lifengming.rpc.common.util.NetUtils;
import com.lifengming.rpc.server.listener.SpringInitApplicationListener;
import com.lifengming.rpc.server.netty.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lifengming
 * @since 2019.10.28
 */
@Configuration
public class RpcServerBootstrap {
    /**
     * 初始化RpcServer
     *
     * @return
     */
    @Bean
    public RpcServer rpcServer() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert inetAddress != null;
        return new RpcServer(inetAddress.getHostAddress(), NetUtils.getAvailablePort(inetAddress.getHostAddress(), 7777, 10000));
    }

    /**
     * 监听Spring容器初始化完毕事件（ContextRefreshedEvent）
     *
     * @return
     */
    @Bean
    public SpringInitApplicationListener springInitApplicationListener() {
        return new SpringInitApplicationListener();
    }

}
