package com.xiao.users.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import org.springframework.data.domain.Page;

public interface UserService {

    void createUser(UserDto userDto) throws JsonProcessingException;

    UserDto findUserById(Long id);

    Page<UserDto> findAllUser(int pages, int pageSize);

    UserDto updateUser(Long userId, UserUpdateDto userDto) throws JsonProcessingException;

    void deleteUser(Long userId) throws JsonProcessingException;

}