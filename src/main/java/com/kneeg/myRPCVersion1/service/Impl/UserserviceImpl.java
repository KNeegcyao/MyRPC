package com.kneeg.myRPCVersion1.service.Impl;

import com.kneeg.myRPCVersion1.common.User;
import com.kneeg.myRPCVersion1.service.UserService;

import java.util.Random;
import java.util.UUID;


public class UserserviceImpl implements UserService {
    @Override
    public User getById(Integer id) {
        System.out.println("客户端查询了"+id+"的用户");
        //模拟从数据库中查询数据
        User user = new User(id, UUID.randomUUID().toString(), new Random().nextBoolean());
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功："+user);
        return 1;
    }


}
