package com.kneeg.myRPCVersion1.service;

import com.kneeg.myRPCVersion1.common.User;

public interface UserService {
    User getById(Integer id);
    Integer insertUserId(User user);

}
