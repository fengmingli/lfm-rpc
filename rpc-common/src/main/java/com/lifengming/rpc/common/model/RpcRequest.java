package com.lifengming.rpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <p>对于请求的封装<p/>
 * @author lifengming
 * @since 2019.09.17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    private String requestId;
    private String interfaceName;
    private String methodName;
    private String className;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
