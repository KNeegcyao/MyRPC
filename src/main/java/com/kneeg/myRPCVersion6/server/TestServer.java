package com.kneeg.myRPCVersion6.server;


import com.kneeg.myRPCVersion6.service.BlogService;
import com.kneeg.myRPCVersion6.service.Impl.BlogServiceImpl;
import com.kneeg.myRPCVersion6.service.Impl.UserServiceImpl;
import com.kneeg.myRPCVersion6.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {
    private static final Logger logger = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

        logger.info("🚀 启动服务端 | 地址: 127.0.0.1:8899");
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",8899);
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);

        RPCServer RPCServer = new NettyRPCServer(serviceProvider);
        RPCServer.start(8899);
    }
}