package com.lfm.rpc.core.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lifengming
 * @since 2019.09.17
 */
@Setter
@Getter
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 5162527258920719229L;
    private String className;
    private String methodName;
    private Object[] parameters;
}
