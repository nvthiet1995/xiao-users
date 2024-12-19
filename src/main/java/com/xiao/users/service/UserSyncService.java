package com.xiao.users.service;

import com.xiao.users.entity.User;

public interface UserSyncService {

    void syncUserToAuthServer(User syncUser, String actionType);

}