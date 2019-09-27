package com.lfm.rpc.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>注解在api接口类，表示提供服务的api<p/>
 *
 * @author lifengming
 * @since 2019.09.17
 */
@Target(ElementType.TYPE)//接口、类、枚举、注解
@Retention(RetentionPolicy.RUNTIME)
@Component
@Inherited//允许子类继承
public @interface LfmRpcService {
    //注解类的反射
    Class<?> value();
}
