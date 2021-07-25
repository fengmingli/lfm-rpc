package com.lifengming.rpc.client.netty;

import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author lifengming
 * @date 2021.07.25
 */
@Slf4j
public class RequestHandler extends ChannelInboundHandlerAdapter {

    /**
     * 等待通道建立最大时间
     */
    static final int CHANNEL_WAIT_TIME = 4;
    /**
     * 等待响应最大时间
     */
    static final int RESPONSE_WAIT_TIME = 8;

    private volatile Channel channel;

    private final String remoteAddress;

    private static final Map<String, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();

    private final MessageProtocol messageProtocol;

    private final CountDownLatch latch = new CountDownLatch(1);

    public RequestHandler(MessageProtocol messageProtocol,String remoteAddress) {
        this.messageProtocol = messageProtocol;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        latch.countDown();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Connect to server successfully:{}", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("Client reads message:{}", msg);
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] resp = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(resp);
        // 手动回收
        ReferenceCountUtil.release(byteBuf);
        RpcResponse response = messageProtocol.deserialize(resp,RpcResponse.class);
        RpcFuture<RpcResponse> future = REQUEST_MAP.get(response.getRequestId());
        future.setResponse(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("Exception occurred:{}", cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("channel inactive with remoteAddress:[{}]",remoteAddress);
        NettyRpcClient.connectedServerNodes.remove(remoteAddress);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    public RpcResponse sendRequest(RpcRequest request) {
        RpcResponse response;
        RpcFuture<RpcResponse> future = new RpcFuture<>();
        REQUEST_MAP.put(request.getRequestId(), future);
        try {
            byte[] data = messageProtocol.serialize(request);
            ByteBuf reqBuf = Unpooled.buffer(data.length);
            reqBuf.writeBytes(data);
            if (latch.await(CHANNEL_WAIT_TIME, TimeUnit.SECONDS)){
                channel.writeAndFlush(reqBuf);
                // 等待响应
                response = future.get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
            }else {
                throw new RpcException("establish channel time out");
            }
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        } finally {
            REQUEST_MAP.remove(request.getRequestId());
        }
        return response;
    }

}
