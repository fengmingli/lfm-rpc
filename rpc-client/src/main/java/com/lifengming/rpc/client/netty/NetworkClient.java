package com.lifengming.rpc.client.netty;

import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;

/**
 * @author lifengming
 * @date 2021.07.25
 */
public interface NetworkClient {
    /**
     * 发送请求
     *
     * @param data 数据流
     * @param service 服务信息
     * @return 二进制流
     * @throws InterruptedException -
     */
    byte[] sendRequest(byte[] data, Service service) throws InterruptedException;

    /**
     * 发送请求
     *
     * @param rpcRequest 请求
     * @param service 服务信息
     * @param messageProtocol 消息协议
     * @return 请求的响应
     */
    RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol);


}
