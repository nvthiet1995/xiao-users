package com.xiao.users.mapper;

import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    User userUpdateDtoToUser(UserUpdateDto user);
    default String mapNullableString(String value) {
        return value != null ? value : "";
    }

}