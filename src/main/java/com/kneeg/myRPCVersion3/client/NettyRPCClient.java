package com.kneeg.myRPCVersion3.client;

import com.kneeg.myRPCVersion3.common.RPCRequest;
import com.kneeg.myRPCVersion3.common.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

/**
 * 实现NettyRPCClient接口
 */
public class NettyRPCClient implements RPCClient {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private String host;
    private int port;
    public NettyRPCClient(String host,int port) throws InterruptedException {
        this.host=host;
        this.port=port;
        ChannelFuture future = bootstrap.connect(host, port).sync();
        future.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("✅ 已连接到服务端: " + host + ":" + port);
            } else {
                System.err.println("❌ 连接失败: " + f.cause());
            }
        });
    }
    //netty客户端初始化，重复使用
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    /**
     * 这里需要操作一下，因为netty的传输都是异步的，你发送request，会立刻返回，而不是想要的相应的response
     * @param request
     * @return
     */
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
       try {
           ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
           Channel channel = channelFuture.channel();
           //发送数据
           channel.writeAndFlush(request);
           channel.closeFuture().sync();
           //阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在handler中设置）
           //AttributeKey是，线程隔离的，不会由线程安全问题
           //实际上不应该通过阻塞，可通过回调函数
           AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
           RPCResponse response = channel.attr(key).get();
           System.out.println(response);
           return response;
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       return null;
    }
}
