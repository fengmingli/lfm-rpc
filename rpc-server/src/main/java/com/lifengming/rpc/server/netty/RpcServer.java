package com.lifengming.rpc.server.netty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lifengming
 * @since 2019.10.15
 */

@Setter
@Getter
public abstract class RpcServer {
    /**
     * 服务端口
     */
    protected int port;

    /**
     * 服务协议
     */
    protected String protocol;

    /**
     * 请求处理者
     */
    protected RequestHandler requestHandler;


    public RpcServer(int port, String protocol, RequestHandler requestHandler) {
        this.port = port;
        this.protocol = protocol;
        this.requestHandler = requestHandler;
    }

    /**
     * 开启服务
     */
    public abstract void start();

    /**
     * 关闭服务
     */
    public abstract void stop();
}
