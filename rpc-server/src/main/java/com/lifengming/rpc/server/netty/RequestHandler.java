package com.lifengming.rpc.server.netty;

import com.lifengming.rpc.common.constans.RpcStatusEnum;
import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import com.lifengming.rpc.common.model.ServiceObject;
import com.lifengming.rpc.register.ServerRegister;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;

import java.lang.reflect.Method;

/**
 * 请求处理
 *
 * @author lifengming
 * @date 2021.07.24
 */
public class RequestHandler {

    private final MessageProtocol protocol;


    private final ServerRegister serverRegister;

    public RequestHandler(MessageProtocol protocol, ServerRegister serverRegister) {
        this.protocol = protocol;
        this.serverRegister = serverRegister;
    }


    public byte[] handleRequest(byte[] data) throws Exception {
        // 1.反系列化消息
        RpcRequest req = this.protocol.deserialize(data, RpcRequest.class);
        // 2.查找服务对应
        ServiceObject so = serverRegister.getServiceObjByServiceName(req.getServiceName());
        RpcResponse response;
        if (so == null) {
            response = new RpcResponse();
            //服务没有发现
            response.setRpcStatus(RpcStatusEnum.NOT_FOUND);

        } else {
            try {
                // 3.反射调用对应的方法过程
                Method method = so.getClazz().getMethod(req.getMethodName(), req.getParameterTypes());
                method.setAccessible(true);
                Object result = method.invoke(so.getServerId(), req.getParameters());
                response = new RpcResponse();
                response.setRpcStatus(RpcStatusEnum.SUCCESS);
                response.setResult(result);
            } catch (Exception e) {
                response = new RpcResponse();
                response.setRpcStatus(RpcStatusEnum.ERROR);
                response.setException(e);
            }
        }
        //4、响应消息，进行序列化
        response.setRequestId(req.getRequestId());
        return this.protocol.serialize(response);
    }
}
