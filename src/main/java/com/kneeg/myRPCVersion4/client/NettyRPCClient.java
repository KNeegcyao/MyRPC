package com.kneeg.myRPCVersion4.client;

import com.kneeg.myRPCVersion4.common.RPCRequest;
import com.kneeg.myRPCVersion4.common.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

/**
 * 实现NettyRPCClient接口
 */
public class NettyRPCClient implements RPCClient {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private Channel channel;

    private String host;
    private int port;

    public NettyRPCClient(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() throws InterruptedException {
        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel(); // 保存连接后的 Channel
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("✅ 已连接到服务端: " + host + ":" + port);
            } else {
                System.err.println("❌ 连接失败: " + f.cause());
            }
        });
    }

    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        try {
            // 使用已有的 channel 发送数据
            channel.writeAndFlush(request).sync();
            // 获取响应
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            return channel.attr(key).get();
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
