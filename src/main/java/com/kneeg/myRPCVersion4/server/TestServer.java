package com.kneeg.myRPCVersion4.server;


import com.kneeg.myRPCVersion4.server.NettyRPCServer;
import com.kneeg.myRPCVersion4.service.BlogService;
import com.kneeg.myRPCVersion4.service.Impl.BlogServiceImpl;
import com.kneeg.myRPCVersion4.service.Impl.UserServiceImpl;
import com.kneeg.myRPCVersion4.service.UserService;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);

        RPCServer RPCServer = new NettyRPCServer(serviceProvider);
        RPCServer.start(8899);
    }
}