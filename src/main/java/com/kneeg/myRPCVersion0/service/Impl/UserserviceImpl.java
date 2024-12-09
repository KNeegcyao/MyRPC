package com.kneeg.myRPCVersion0.service.Impl;

import com.kneeg.myRPCVersion0.common.User;
import com.kneeg.myRPCVersion0.service.UserService;
import jdk.management.resource.ResourceType;

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
}
