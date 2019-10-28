package com.lifengming.rpc.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
    private static ApplicationContext applicationContext = null;

    /**
     * 当ContextLoaderListener加载配置文件，创建Spring容器后，会将容器对象自动注入进来（就是自动调用下面的方法）
     **/

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }


    public static Object getBean(Class beanName) {
        return applicationContext.getBean(beanName);
    }

    public static Map<String, Object> getBeansByAnnotation(Class<? extends Annotation> clz) {
        return applicationContext.getBeansWithAnnotation(clz);
    }
}
