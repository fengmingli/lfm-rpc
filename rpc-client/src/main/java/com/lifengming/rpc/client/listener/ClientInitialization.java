package com.lifengming.rpc.client.listener;

import com.lifengming.rpc.client.LfmRpcClientProxy;
import com.lifengming.rpc.common.annotation.RpcReference;
import com.lifengming.rpc.register.localcache.ServiceLocalCache;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author lifengming
 * @since 2019.10.15
 */
@Slf4j
public class ClientInitialization implements ApplicationListener<ContextRefreshedEvent> {

    private final LfmRpcClientProxy clientProxyFactory;

    public ClientInitialization(LfmRpcClientProxy clientProxyFactory) {
        this.clientProxyFactory = clientProxyFactory;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        // Spring启动完毕过后会收到一个事件通知
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            //获取SpringContext 上下文
            ApplicationContext context = event.getApplicationContext();
            // 引入远程服务
            referenceService(context);
            // 注册子节点监听
            clientProxyFactory.getServerDiscovery().watchChildNode(clientProxyFactory.getServerDiscovery());
        }
    }

    private void referenceService(ApplicationContext context) {
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            Class<?> clazz = context.getType(name);
            if (Objects.isNull(clazz)) {
                continue;
            }
            //获取服务的所有字段
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // 找出所有标记了RpcReference注解的属性
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                if (rpcReference == null) {
                    continue;
                }
                //获取该字段所属类
                Class<?> fieldClass = field.getType();
                Object object = context.getBean(name);
                field.setAccessible(true);
                try {
                    //替换该服务的值为动态代理
                    field.set(object, clientProxyFactory.clientProxy(fieldClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                //服务名本地缓存
                ServiceLocalCache.SERVICE_CLASS_NAMES.add(fieldClass.getName());
            }
        }
    }
}
