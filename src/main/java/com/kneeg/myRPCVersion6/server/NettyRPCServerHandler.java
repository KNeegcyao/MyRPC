package com.kneeg.myRPCVersion6.server;

import com.kneeg.myRPCVersion6.common.RPCRequest;
import com.kneeg.myRPCVersion6.common.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 因为是服务器端，我们知道接受到请求格式是RPCRequest
 * Object类型也行，强制转型就行
 */

/**
 * 因为是服务器端，我们知道接受到请求格式是RPCRequest
 * Object类型也行，强制转型就行
 */
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyRPCServerHandler.class);

    private ServiceProvider serviceProvider;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
        logger.info("📥 接收到请求 | 接口: {} | 方法: {}", msg.getInterfaceName(), msg.getMethodName());
        RPCResponse response = getResponse(msg);
        ctx.writeAndFlush(response).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("📤 响应发送成功 | 客户端: {}", ctx.channel().remoteAddress());
            } else {
                logger.error("❌ 响应发送失败 | 原因: {}", future.cause().getMessage());
            }
            ctx.close();
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("🔥 通信异常 | 客户端: {} | 错误: {}", ctx.channel().remoteAddress(), cause.getMessage());
        ctx.close();
    }

    RPCResponse getResponse(RPCRequest request) {
        // 得到服务名
        String interfaceName = request.getInterfaceName();
        // 得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RPCResponse.fail();
        }
    }
}