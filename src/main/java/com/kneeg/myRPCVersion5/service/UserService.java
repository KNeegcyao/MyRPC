package com.kneeg.myRPCVersion5.service;

import com.kneeg.myRPCVersion5.common.User;

public interface UserService {
    User getById(Integer id);
    Integer insertUserId(User user);

}
