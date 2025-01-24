package com.kneeg.myRPCVersion3.server;

import com.kneeg.myRPCVersion3.service.Impl.BlogServiceImpl;
import com.kneeg.myRPCVersion3.service.Impl.UserServiceImpl;


public class TestServer {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        BlogServiceImpl blogService = new BlogServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);
        RPCServer RPCServer = new NettyRPCServer(serviceProvider);
        RPCServer.start(8899);
    }
}
