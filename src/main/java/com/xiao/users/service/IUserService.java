package com.xiao.users.service;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {

    void createUser(UserDto userDto);

    UserDto findUserById(Long id);

    Page<UserDto> findAllUser(int pages, int pageSize);

    UserDto updateUser(Long userId, UserDto userDto);

}