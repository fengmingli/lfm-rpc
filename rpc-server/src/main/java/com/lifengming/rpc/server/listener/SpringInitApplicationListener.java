package com.lifengming.rpc.server.listener;

import com.lifengming.rpc.common.annotation.RpcService;
import com.lifengming.rpc.common.util.ApplicationHelper;
import com.lifengming.rpc.server.netty.RpcServer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.10.15
 */
@Slf4j
public class SpringInitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ConcurrentHashMap<String, Object> handlerMap = new ConcurrentHashMap<>(16);

    @Autowired
    private RpcServer rpcServer;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        Map<String, Object> beanMap = ApplicationHelper.getBeansByAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            //只有value不是FactoryBean（是实现类），才注册到注册中心
            if (!(entry.getValue() instanceof Proxy)) {
                handlerMap.put(entry.getValue().getClass().getInterfaces()[0].getName(), entry.getValue());
            }
        }

        //只有有服务发布，才会启动netty并注册
        if (handlerMap.size() != 0) {
            //让新线程去启动netty，防止netty阻塞 【监听关闭】 导致主线程阻塞
            threadPoolTaskExecutor.execute(() -> rpcServer.startServer(handlerMap));
        }
    }
}
