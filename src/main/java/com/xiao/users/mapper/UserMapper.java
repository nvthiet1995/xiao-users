package com.xiao.users.mapper;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

//    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

}