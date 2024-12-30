package com.xiao.users.util;

import com.xiao.users.dto.RoleDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;

import java.util.HashSet;
import java.util.Set;

public class UserUtil {

    public static UserUpdateDto buildUserUpdateDto() {
        Set<RoleDto> roleDtoSet = new HashSet<>();
        roleDtoSet.add(RoleUtil.buildRoleDto());
        return UserUpdateDto.builder()
                .username("abc_update")
                .emailAddress("abc_update@gmail.com")
                .password("passwordUpdate")
                .roles(roleDtoSet)
                .build();
    }

    public static UserDto buildUserDto() {
        Set<RoleDto> roleDtoSet = new HashSet<>();
        roleDtoSet.add(RoleUtil.buildRoleDto());
        return UserDto.builder()
                .username("abc")
                .emailAddress("abc@gmail.com")
                .password("abcPassword")
                .roles(roleDtoSet)
                .build();
    }

    public static User buildUser() {
        return User.builder()
                .username("abc")
                .emailAddress("abc@gmail.com")
                .password("abcPassword")
                .build();
    }

}
