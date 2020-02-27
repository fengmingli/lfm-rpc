package com.lifengming.rpc.server;

import com.lifengming.rpc.core.annotation.LfmRpcService;
import com.lifengming.rpc.core.thread.ThreadPoolExecutorFactory;
import com.lifengming.rpc.core.util.ApplicationHelper;
import com.lifengming.rpc.server.netty.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.11.25
 */
public class RpcServerInitialization {
    private ConcurrentHashMap<String, Object> handlerMap = new ConcurrentHashMap<>(16);

    @Autowired
    private RpcServer rpcServer;

    void startServer() {
        Map<String, Object> beanMap = ApplicationHelper.getBeansByAnnotation(LfmRpcService.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            //只有value不是FactoryBean（是实现类），才注册到注册中心
            if (!(entry.getValue() instanceof Proxy)) {
                handlerMap.put(entry.getValue().getClass().getInterfaces()[0].getName(), entry.getValue());
            }
        }

        //只有有服务发布，才会启动netty并注册
        if (handlerMap.size() != 0) {
            //让新线程去启动netty，防止netty阻塞 【监听关闭】 导致主线程阻塞
            ThreadPoolExecutorFactory.getInstance().executeTask(() -> rpcServer.doStartServer(handlerMap));
        }
    }
}
