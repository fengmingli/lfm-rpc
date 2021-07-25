package com.lifengming.rpc.client.netty;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.lifengming.rpc.common.model.RpcRequest;
import com.lifengming.rpc.common.model.RpcResponse;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.serialization.serialize.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lifengming
 * @date 2021.07.25
 */
@Slf4j
public class NettyRpcClient implements NetworkClient {

    /**
     * 已连接的服务缓存
     * key: 服务地址，格式：ip:port
     */
    public static Map<String, RequestHandler> connectedServerNodes = new ConcurrentHashMap<>();

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(4, 10, 200,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder()
            .setNamePrefix("rpcClient-%d")
            .build());

    private final EventLoopGroup loopGroup = new NioEventLoopGroup(4);

    @Override
    public byte[] sendRequest(byte[] data, Service service) throws InterruptedException {
        return new byte[0];
    }

    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest, Service service, MessageProtocol messageProtocol) {
        String address = service.getAddress();
        synchronized (address) {
            if (connectedServerNodes.containsKey(address)) {
                RequestHandler requestHandler = connectedServerNodes.get(address);
                log.info("usage current connect ....");
                return requestHandler.sendRequest(rpcRequest);
            }

            String[] addrInfo = address.split(":");
            final String serverAddress = addrInfo[0];
            final String serverPort = addrInfo[1];
            final RequestHandler handler = new RequestHandler(messageProtocol, address);
            THREAD_POOL.submit(() -> {
                        // 配置客户端
                        Bootstrap b = new Bootstrap();
                        b.group(loopGroup).channel(NioSocketChannel.class)
                                .option(ChannelOption.TCP_NODELAY, true)
                                .handler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                                        ChannelPipeline pipeline = socketChannel.pipeline();
                                        pipeline.addLast(handler);
                                    }
                                });
                        // 启用客户端连接
                        ChannelFuture channelFuture = b.connect(serverAddress, Integer.parseInt(serverPort));
                        channelFuture.addListener((ChannelFutureListener) channelFuture1 -> connectedServerNodes.put(address, handler));
                    }
            );
            log.info("usage new connect ...");
            return handler.sendRequest(rpcRequest);
        }
    }
}
