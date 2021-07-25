package com.lifengming.rpc.server.netty;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * netty 服务通信
 *
 * @author lifengming
 * @date 2021.07.24
 */
public class NettyRpcServer extends RpcServer {
    private static final Logger log = LoggerFactory.getLogger(NettyRpcServer.class);

    private Channel channel;

    private static final ExecutorService POOL = new ThreadPoolExecutor(4, 8,
            200, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNamePrefix("rpcServer-%d").build());

    public NettyRpcServer(int port, String protocol, RequestHandler requestHandler) {
        super(port, protocol, requestHandler);
    }

    @Override
    public void start() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new RpcServerChannelInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //开启服务
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.debug("ServerRegister started successfully.");
            channel = channelFuture.channel();
            // 等待服务通道关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("ServerRegister shutdown!", e);
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        this.channel.close();
    }

    private class RpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline cp = channel.pipeline();
            cp.addLast(new ChannelRequestHandler());
        }
    }

    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.debug("Channel active :{}", ctx);
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
            POOL.submit(() -> {
                try {
                    log.debug("the server receives message :{}", msg);
                    ByteBuf byteBuf = (ByteBuf) msg;
                    // 消息写入reqData
                    byte[] reqData = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(reqData);
                    // 手动回收
                    ReferenceCountUtil.release(byteBuf);
                    byte[] respData = requestHandler.handleRequest(reqData);
                    ByteBuf respBuf = Unpooled.buffer(respData.length);
                    respBuf.writeBytes(respData);
                    log.debug("Send response:{}", respBuf);
                    ctx.writeAndFlush(respBuf)
                            .addListener((ChannelFutureListener) channelFuture -> log.info("server Send response for request: {}",""));
                } catch (Exception e) {
                    log.error("server read exception", e);
                }
            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            log.error("Exception occurred:{}", cause.getMessage());
            ctx.close();
        }
    }
}
