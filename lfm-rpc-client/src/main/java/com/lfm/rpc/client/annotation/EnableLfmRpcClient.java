package com.lfm.rpc.client.annotation;

import java.lang.annotation.*;

/**
 * @author lifengming
 * @since 2019.09.18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableLfmRpcClient {
}
