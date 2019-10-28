package com.lifengming.rpc.client.annotation;

import java.lang.annotation.*;

/**
 * @author lifengming
 * @since 2019.09.18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableLfmRpcClient {
    //引用的api接口的位置
    String[] basePackages() default {};
}
