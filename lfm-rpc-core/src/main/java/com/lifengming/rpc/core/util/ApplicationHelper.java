package com.lifengming.rpc.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author lifengming
 * @since 2019.10.15
 */
@Component
public class ApplicationHelper implements ApplicationContextAware {
    /**
     * 声明Spring容器对象
     **/
    private static ApplicationHelper ctx = new ApplicationHelper();

    private ApplicationContext applicationContext;

    private ApplicationHelper(){}
    /**
     * 当ContextLoaderListener加载配置文件，创建Spring容器后，会将容器对象自动注入进来（就是自动调用下面的方法）
     **/

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ctx.applicationContext = applicationContext;
    }
    /**
     * 获取applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return ctx.applicationContext;
    }

    public static Object getBean(Class<?> beanName) {
        return getApplicationContext().getBean(beanName);
    }

    public static Map<String, Object> getBeansByAnnotation(Class<? extends Annotation> clz) {
        return getApplicationContext().getBeansWithAnnotation(clz);
    }
}
