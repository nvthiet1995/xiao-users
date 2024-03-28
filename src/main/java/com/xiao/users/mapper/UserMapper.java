package com.xiao.users.mapper;

import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(ignore = true, target = "password")
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    User userUpdateDtoToUser(UserUpdateDto user);

}