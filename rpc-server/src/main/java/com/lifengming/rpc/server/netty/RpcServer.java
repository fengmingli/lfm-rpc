package com.lifengming.rpc.server.netty;

import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import com.lifengming.rpc.common.model.ServiceAddress;
import com.lifengming.rpc.register.servicecenter.consulservice.ConsulServiceRegistryImpl;
import com.lifengming.rpc.remoting.netty.code.RpcDecoder;
import com.lifengming.rpc.remoting.netty.code.RpcEncoder;
import com.lifengming.rpc.serialization.serialize.protobuf.ProtobufSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.10.15
 */
@Slf4j
@RequiredArgsConstructor
public class RpcServer {

    @NonNull
    private final String serverIp;

    private final int serverPort;

    @Autowired
    public ConsulServiceRegistryImpl consulServiceRegistry;

    public void startServer(ConcurrentHashMap<String, Object> handlerMap) {
        //get IP and port
        log.debug("Starting server on port: {}", serverPort);
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new RpcServerChannelInitializer(handlerMap))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(serverPort).sync();

            registerServices(handlerMap);
            log.info("Server started");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("Server shutdown!", e);
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    private void registerServices(ConcurrentHashMap<String, Object> handlerMap) {
        if (consulServiceRegistry != null) {
            for (String interfaceName : handlerMap.keySet()) {
                consulServiceRegistry.serviceRegister(interfaceName, new ServiceAddress(serverIp, serverPort));
                log.info("注册服务:{}，注册地址:{},:{}", interfaceName, serverIp, serverPort);
            }
        }
    }

    @AllArgsConstructor
    private static class RpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        private final ConcurrentHashMap<String, Object> handlerMap;

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline cp = channel.pipeline();
            cp.addLast(new RpcDecoder(RpcRequest.class, new ProtobufSerializer()));
            cp.addLast(new RpcEncoder(RpcResponse.class, new ProtobufSerializer()));
            cp.addLast(new RpcServerHandler(handlerMap));
        }
    }

}
