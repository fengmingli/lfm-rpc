//package com.lifengming.rpc.remoting.netty;
//
//
//import com.lifengming.rpc.common.model.RpcRequest;
//import com.lifengming.rpc.common.model.RpcResponse;
//import com.lifengming.rpc.remoting.netty.code.RpcDecoder;
//import com.lifengming.rpc.remoting.netty.code.RpcEncoder;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFutureListener;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.ChannelPipeline;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//
//import java.net.InetSocketAddress;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author lifengming
// * @since 2019.10.14
// */
//public class ChannelManager {
//    /**
//     * single get Channel
//     */
//    private static volatile ChannelManager channelManager = null;
//
//    private ChannelManager() {
//    }
//
//    public static ChannelManager getChannelManagerInstance() {
//        if (channelManager == null) {
//            synchronized (ChannelManager.class) {
//                if (channelManager == null) {
//                    channelManager = new ChannelManager();
//                }
//            }
//        }
//        return channelManager;
//    }
//
//    private final ConcurrentHashMap<InetSocketAddress, Channel> channels = new ConcurrentHashMap<>();
//
//    public Channel getChannel(InetSocketAddress inetSocketAddress) {
//        Channel channel = channels.get(inetSocketAddress);
//
//        if (channel == null) {
//            EventLoopGroup workGroup = new NioEventLoopGroup();
//            try {
//                Bootstrap bootstrap = new Bootstrap();
//                bootstrap.group(workGroup)
//                        .channel(NioSocketChannel.class)
//                        .handler(new LfmRpcChannelInitializer())
//                        .option(ChannelOption.SO_KEEPALIVE, true);
//
//                channel = bootstrap.connect(new InetSocketAddress(inetSocketAddress.getHostName(), inetSocketAddress.getPort())).sync().channel();
//                registerChannel(inetSocketAddress, channel);
//
//                // Remove the channel for map when it's closed
//                channel.closeFuture().addListener((ChannelFutureListener) future -> removeChannel(inetSocketAddress));
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return channel;
//    }
//
//    private void removeChannel(InetSocketAddress inetSocketAddress) {
//        channels.remove(inetSocketAddress);
//    }
//
//    private void registerChannel(InetSocketAddress inetSocketAddress, Channel channel) {
//        channels.put(inetSocketAddress, channel);
//    }
//
//    private static class LfmRpcChannelInitializer extends ChannelInitializer<SocketChannel> {
//        @Override
//        protected void initChannel(SocketChannel socketChannel) throws Exception {
//            ChannelPipeline cp = socketChannel.pipeline();
//            cp.addLast(new RpcEncoder(RpcRequest.class, new ProtobufSerializer()));
//            cp.addLast(new RpcDecoder(RpcResponse.class, new ProtobufSerializer()));
//            cp.addLast(new RpcResponseHandler());
//        }
//    }
//
//    private static class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
//
//        @Override
//        public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
//            ResponseFutureManager.getResponseFutureManagerInstance().futureDone(response);
//        }
//
//        @Override
//        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            ctx.close();
//        }
//
//    }
//}
