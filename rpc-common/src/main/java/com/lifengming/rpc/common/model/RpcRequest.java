package com.lifengming.rpc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>对于请求的封装<p/>
 * @author lifengming
 * @since 2019.09.17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    private String requestId;
    private final Map<String,String> headers = new HashMap<>();
    private String serviceName;
    private String methodName;
    private String className;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
