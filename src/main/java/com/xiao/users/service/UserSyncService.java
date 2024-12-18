package com.xiao.users.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;
import org.springframework.data.domain.Page;

public interface UserSyncService {

    void syncUserToAuthServer(User syncUser, String actionType) throws JsonProcessingException;

}