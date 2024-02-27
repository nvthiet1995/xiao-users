package com.xiao.users.service;

import com.xiao.users.dto.UserDto;

import java.util.List;

public interface IUserService {

    void createUser(UserDto userDto);

    UserDto findUserById(Long id);

    List<UserDto> fetchAllUsers();

}