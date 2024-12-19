package com.kneeg.myRPCVersion2.client;

import com.kneeg.myRPCVersion2.common.Blog;
import com.kneeg.myRPCVersion2.common.User;
import com.kneeg.myRPCVersion2.service.BlogService;
import com.kneeg.myRPCVersion2.service.UserService;

/**
 * 客户端
 */
public class RPCClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 8899);
        UserService proxy = clientProxy.getProxy(UserService.class);
        BlogService blogService = clientProxy.getProxy(BlogService.class);

        //服务的方法1
        User user = proxy.getById(10);
        System.out.println("从服务端得到的user为："+user);
        //服务的方法2
        User user1 = new User(100,"张三",true);
        Integer i = proxy.insertUserId(user);
        System.out.println("向服务端插入数据："+i);
        // 客户中添加新的测试用例
        Blog blogById = blogService.getBlogById(10000);
        System.out.println("从服务端得到的blog为：" + blogById);
    }
}
