package com.kneeg.myRPCVersion2.server;

import com.kneeg.myRPCVersion2.service.Impl.UserserviceImpl;
import com.kneeg.myRPCVersion2.service.Impl.BlogServiceImpl;

import java.util.HashMap;

public class TestServer {
    public static void main(String[] args) {
        UserserviceImpl userService = new UserserviceImpl();
        BlogServiceImpl blogService = new BlogServiceImpl();
//        HashMap<String, Object> serviceProvide = new HashMap<>();
//        //暴露两个服务接口，即在RPCServer中加一个HashMap
//        serviceProvide.put("com.kneeg.myRPCVersion2.service.UserService",userService);
//        serviceProvide.put("com.kneeg.myRPCVersion2.service.BlogService",blogService);
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);
        serviceProvider.provideServiceInterface(blogService);
        RPCServer RPCServer = new ThreadPoolRPCRPCServer(serviceProvider);
        RPCServer.start(8899);


    }
}
