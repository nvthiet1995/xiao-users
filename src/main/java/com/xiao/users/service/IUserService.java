package com.xiao.users.service;

import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

public interface IUserService {

    void createUser(UserDto userDto);

    UserDto findUserById(Long id);

    Page<UserDto> findAllUser(int pages, int pageSize);

    UserDto updateUser(Long userId, UserUpdateDto userDto);

}