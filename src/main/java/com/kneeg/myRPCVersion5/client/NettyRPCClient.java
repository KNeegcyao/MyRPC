package com.kneeg.myRPCVersion5.client;

import com.kneeg.myRPCVersion5.common.RPCRequest;
import com.kneeg.myRPCVersion5.common.RPCResponse;
import com.kneeg.myRPCVersion5.register.ServiceRegister;
import com.kneeg.myRPCVersion5.register.ZKServiceRegister;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * 实现NettyRPCClient接口
 */
public class NettyRPCClient implements RPCClient {
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
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channel=channelFuture.channel();
            // 使用已有的 channel 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 实际上不应通过阻塞，可通过回调函数，后面可以再进行优化
            // 获取响应
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            RPCResponse response=channel.attr(key).get();
            System.out.println(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
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
