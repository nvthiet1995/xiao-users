package com.xiao.users.util;

import com.xiao.users.dto.RoleDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;

import java.util.HashSet;
import java.util.Set;

public class UserUtil {

    public static UserUpdateDto buildUserUpdateDto() {
        return UserUpdateDto.builder()
                .username("abc_update")
                .emailAddress("abc_update@gmail.com")
                .password("passwordUpdate")
                .build();
    }

    public static UserDto buildUserDto() {
        Set<RoleDto> roleDtos = new HashSet<>();
        roleDtos.add(RoleDto.builder().id(1L).build());
        return new UserDto("abc_username", "password@123", "abc@gmail.com", roleDtos);
    }

    public static User buildUser() {
        return User.builder()
                .username("abc")
                .emailAddress("abc@gmail.com")
                .password("abcPassword")
                .build();
    }

}
