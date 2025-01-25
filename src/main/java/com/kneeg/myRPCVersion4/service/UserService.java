package com.kneeg.myRPCVersion4.service;

import com.kneeg.myRPCVersion4.common.User;

public interface UserService {
    User getById(Integer id);
    Integer insertUserId(User user);

}
