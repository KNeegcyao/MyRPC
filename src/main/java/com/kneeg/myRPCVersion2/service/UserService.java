package com.kneeg.myRPCVersion2.service;

import com.kneeg.myRPCVersion2.common.User;

public interface UserService {
    User getById(Integer id);
    Integer insertUserId(User user);

}
