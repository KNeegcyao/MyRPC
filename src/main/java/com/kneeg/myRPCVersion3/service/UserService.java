package com.kneeg.myRPCVersion3.service;

import com.kneeg.myRPCVersion3.common.User;

public interface UserService {
    User getById(Integer id);
    Integer insertUserId(User user);

}
