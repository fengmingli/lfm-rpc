package com.lfm.rpc.server.netty;

import com.lfm.rpc.core.model.RpcRequest;
import com.lfm.rpc.core.model.RpcResponse;
import com.lfm.rpc.core.model.ServiceAddress;
import com.lfm.rpc.core.serialization.coder.RpcDecoder;
import com.lfm.rpc.core.serialization.coder.RpcEncoder;
import com.lfm.rpc.core.serialization.serialize.ProtobufSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.10.15
 */
@Slf4j
@Component
public class RpcServer {
    @NonNull
    private String serverIp;

    private int serverPort;


    public void startServer(ConcurrentHashMap<String, Object> handlerMap) {
        //get IP and port
        log.debug("Starting server on port: {}", serverPort);
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new LfmRpcServerChannelInitializer(handlerMap))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(serverPort).sync();
            log.info("Server started");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("Server shutdown!", e);
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }


    @AllArgsConstructor
    private static class LfmRpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        private ConcurrentHashMap<String, Object> handlerMap;

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline cp = channel.pipeline();
            cp.addLast(new RpcDecoder(RpcRequest.class, new ProtobufSerializer()));
            cp.addLast(new RpcEncoder(RpcResponse.class, new ProtobufSerializer()));
            cp.addLast(new RpcServerHandler(handlerMap));
        }
    }

}
