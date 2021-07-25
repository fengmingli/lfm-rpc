package com.lifengming.rpc.client.listener;

import com.lifengming.rpc.client.LfmRpcClientProxy;
import com.lifengming.rpc.common.annotation.RpcReference;
import com.lifengming.rpc.common.constans.RpcConstant;
import com.lifengming.rpc.register.localcache.ServiceLocalCache;
import com.lifengming.rpc.register.zookeeper.ZookeeperChildListenerImpl;
import com.lifengming.rpc.register.zookeeper.ZookeeperServerDiscovery;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
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
            // 注入Service
            referenceService(context);
        }
    }

    private void referenceService(ApplicationContext context) {
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            Class<?> clazz = context.getType(name);
            if (Objects.isNull(clazz)) {
                continue;
            }
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // 找出所有标记了RpcReference注解的属性
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                if (rpcReference == null) {
                    continue;
                }

                Class<?> fieldClass = field.getType();
                Object object = context.getBean(name);
                field.setAccessible(true);
                try {
                    field.set(object, clientProxyFactory.clientProxy (fieldClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                ServiceLocalCache.SERVICE_CLASS_NAMES.add(fieldClass.getName());
            }
        }

        // 注册子节点监听
        if (clientProxyFactory.getServerDiscovery() instanceof ZookeeperServerDiscovery) {
            ZookeeperServerDiscovery serverDiscovery = (ZookeeperServerDiscovery) clientProxyFactory.getServerDiscovery();
            ZkClient zkClient = serverDiscovery.getZkClient();
            ServiceLocalCache.SERVICE_CLASS_NAMES.forEach(name -> {
                String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + name + "/service";
                zkClient.subscribeChildChanges(servicePath, new ZookeeperChildListenerImpl());
            });
            log.info("subscribe service zk node successfully");
        }
    }
}
