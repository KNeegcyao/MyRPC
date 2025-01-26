package com.kneeg.myRPCVersion5.server;


import com.kneeg.myRPCVersion5.service.BlogService;
import com.kneeg.myRPCVersion5.service.Impl.BlogServiceImpl;
import com.kneeg.myRPCVersion5.service.Impl.UserServiceImpl;
import com.kneeg.myRPCVersion5.service.UserService;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        BlogService blogService = new BlogServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1",8899);
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);

        RPCServer RPCServer = new NettyRPCServer(serviceProvider);
        RPCServer.start(8899);
    }
}