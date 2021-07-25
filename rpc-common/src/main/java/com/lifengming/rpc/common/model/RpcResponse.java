package com.lifengming.rpc.common.model;

import com.lifengming.rpc.common.constans.RpcStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>对于响应的封装<p/>
 * @author lifengming
 * @since 2019.09.17
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    private String requestId;
    private Map<String, String> headers = new HashMap<>();
    private Exception exception;
    private Object result;
    private RpcStatusEnum rpcStatus;
    /**
     * check exception
     * @return exist true;
     */
    public boolean hasException() {
        return exception != null;
    }

}
