package com.kneeg.myRPCVersion6.service;

import com.kneeg.myRPCVersion6.common.User;

public interface UserService {
    User getById(Integer id);
    Integer insertUserId(User user);

}
