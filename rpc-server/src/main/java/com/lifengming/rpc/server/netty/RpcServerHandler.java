package com.lifengming.rpc.server.netty;

import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle the rpc request
 * 实现ChannelHandler接口，自定义拆包/装包逻辑
 * 通过netty，拿到request并去注册中心找到request中的调用接口
 *
 * @author lifengming
 * @since 2019.10.15
 */
@Slf4j
@AllArgsConstructor
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final ConcurrentHashMap<String, Object> handlerMap;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        log.info("Get request:{}", rpcRequest);

        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());

        try {
            Object result = handlerRequest(rpcRequest);
            response.setResult(result);
        } catch (Exception exception) {
            log.error("when handing request err", exception);
            response.setException(exception);
        }
        ctx.writeAndFlush(response)
                .addListener((ChannelFutureListener) channelFuture -> log.info("server Send response for request: {}", rpcRequest.getRequestId()));


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught exception", cause);
        ctx.close();
    }

    private Object handlerRequest(RpcRequest rpcRequest) throws Exception {

        //Get server been
        String interfaceName = rpcRequest.getInterfaceName();
        Object serviceBean = handlerMap.get(interfaceName);

        if (serviceBean == null) {
            throw new RpcException(String.format("No service bean available: %s", interfaceName));
        }

        //Invoke by reflect
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);

        return method.invoke(serviceBean, parameters);
    }
}
