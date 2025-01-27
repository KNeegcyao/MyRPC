package com.kneeg.myRPCVersion6.client;

import com.kneeg.myRPCVersion6.common.RPCRequest;
import com.kneeg.myRPCVersion6.common.RPCResponse;
import com.kneeg.myRPCVersion6.register.ServiceRegister;
import com.kneeg.myRPCVersion6.register.ZKServiceRegister;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 实现NettyRPCClient接口
 */
public class NettyRPCClient implements RPCClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyRPCClient.class);
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private Channel channel;
    private String host;
    private int port;
    private ServiceRegister serviceRegister;
    public NettyRPCClient() throws InterruptedException {
        this.serviceRegister=new ZKServiceRegister();
        //connect();
    }

//    private void connect() throws InterruptedException {
//        ChannelFuture future = bootstrap.connect(host, port).sync();
//        this.channel = future.channel(); // 保存连接后的 Channel
//        future.addListener(f -> {
//            if (f.isSuccess()) {
//                System.out.println("✅ 已连接到服务端: " + host + ":" + port);
//            } else {
//                System.err.println("❌ 连接失败: " + f.cause());
//            }
//        });
//    }

    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        InetSocketAddress address = serviceRegister.serviceDiscovery(request.getInterfaceName());
        host=address.getHostName();
        port=address.getPort();
        try {
            logger.info("🔍 服务发现 | 接口: {} | 可用节点: {}", request.getInterfaceName(), address);
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel=channelFuture.channel();
            // 使用已有的 channel 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            logger.debug("📨 请求已发送 | 目标: {} | 方法: {}", address, request.getMethodName());
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 实际上不应通过阻塞，可通过回调函数，后面可以再进行优化
            // 获取响应
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            return channel.attr(key).get();
        } catch (Exception e) {
            logger.error("⛔ 请求失败 | 接口: {} | 错误: {}", request.getInterfaceName(), e.getMessage());
        }
        return null;
    }

    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }
}
