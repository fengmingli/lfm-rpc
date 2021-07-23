package com.lifengming.rpc.common.exception;

/**
 * @author lifengming
 * @since 2019.09.17
 */
public class RpcException extends RuntimeException  {

    private static final long serialVersionUID = -3594448263939459024L;

    public RpcException(String message) { super(message); }

    public RpcException(String message, Throwable cause) { super(message, cause); }

    public RpcException(Throwable cause) { super(cause); }

}
