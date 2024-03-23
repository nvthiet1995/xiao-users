package com.xiao.users.util;

import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;

public class UserUtil {

    public static UserUpdateDto buildUserUpdateDto() {
        return UserUpdateDto.builder()
                .username("abc_update")
                .emailAddress("abc_update@gmail.com")
                .password("abcPassword_update")
                .build();
    }

    public static UserDto buildUserDto() {
        return UserDto.builder()
                .username("abc")
                .emailAddress("abc@gmail.com")
                .password("abcPassword")
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
