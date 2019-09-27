package com.lfm.rpc.core.model;

import lombok.*;

/**
 * <p>对于请求的封装<p/>
 * @author lifengming
 * @since 2019.09.17
 */
@Setter
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
