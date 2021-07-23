package com.lifengming.rpc.common.model;

import lombok.*;
/**
 * <p>对于响应的封装<p/>
 * @author lifengming
 * @since 2019.09.17
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

}
