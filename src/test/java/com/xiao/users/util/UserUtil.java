package com.xiao.users.util;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;

public class UserUtil {

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
