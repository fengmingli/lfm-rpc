package com.lifengming.rpc.server.listener;

import com.lifengming.rpc.common.annotation.RpcService;
import com.lifengming.rpc.common.model.ServiceObject;
import com.lifengming.rpc.register.ServerRegister;
import com.lifengming.rpc.server.netty.RpcServer;
import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lifengming
 * @since 2019.10.15
 */
public class ServerInitialization implements ApplicationListener<ContextRefreshedEvent> {
    private final RpcServer rpcServer;
    private final ServerRegister serverRegister;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public ServerInitialization(RpcServer rpcServer, ServerRegister serverRegister,ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.rpcServer = rpcServer;
        this.serverRegister = serverRegister;
        this.threadPoolTaskExecutor=threadPoolTaskExecutor;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        // Spring启动完毕过后会收到一个事件通知
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            //获取SpringContext 上下文
            ApplicationContext context = event.getApplicationContext();
            // 开启服务,异步执行
            threadPoolTaskExecutor.execute(()->startServer(context));;
        }
    }


    private void startServer(ApplicationContext context) {
        //寻找需要暴露的服务
        Map<String, Object> rpcServiceMap = context.getBeansWithAnnotation(RpcService.class);
        if (rpcServiceMap.size() > 0) {
            AtomicBoolean startServerFlag = new AtomicBoolean(true);
            for (Object rpcService : rpcServiceMap.values()) {
                try {
                    Class<?> clazz = rpcService.getClass();
                    Class<?>[] interfaces = clazz.getInterfaces();
                    ServiceObject serviceObject;
                    // 如果只实现了一个接口就用父类的className作为服务名
                    // 如果该类实现了多个接口，则用注解里的interfaceName作为服务名
                    if (interfaces.length != 1) {
                        RpcService service = clazz.getAnnotation(RpcService.class);
                        String serviceName = service.interfaceName();
                        if ("".equals(serviceName)) {
                            startServerFlag.set(false);
                            throw new UnsupportedOperationException("The exposed interface is not specific with '" + rpcService.getClass().getName() + "'");
                        }

                        serviceObject = ServiceObject.builder()
                                .serviceName(serviceName)
                                .clazz(Class.forName(serviceName))
                                .serverId(rpcService)
                                .build();
                    } else {
                        Class<?> supperClass = interfaces[0];
                        serviceObject = ServiceObject.builder()
                                .serviceName(supperClass.getName())
                                .clazz(supperClass)
                                .serverId(rpcService)
                                .build();
                    }

                    //服务注册
                    serverRegister.register(serviceObject);

                } catch (Exception e) {
                    //on op
                }
            }

            if (startServerFlag.get()) {
                rpcServer.start();
            }
        }

    }
}
