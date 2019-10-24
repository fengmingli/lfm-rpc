//package com.lifengming.bootstrap;
//
//import com.lfm.rpc.server.netty.RpcServer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
///**
// * @author lifengming
// * @since 2019.10.24
// */
//@Configuration
//public class LfmRpcServerBootstrap {
//
//    /**
//     * 初始化RpcServer
//     *
//     * @return
//     */
//    @Bean
//    public RpcServer rpcServer() {
//        InetAddress inet = null;
//        try {
//            inet = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        RpcServer rpcServer = new RpcServer(inet.getHostAddress(), Integer.valueOf(NetUtils.getAvailablePort(inet.getHostAddress(), 6000, 10000)));
//        return rpcServer;
//    }
//
//    /**
//     * 监听Spring容器初始化完毕事件（ContextRefreshedEvent）
//     *
//     * @return
//     */
////    @Bean
////    public SpringInitApplicationListenner springInitApplicationListenner() {
////        return new SpringInitApplicationListenner();
////    }
//
//}
