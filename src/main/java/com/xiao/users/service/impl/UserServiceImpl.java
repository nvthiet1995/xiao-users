package com.xiao.users.service.impl;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import com.xiao.users.exception.ResourceNotFoundException;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        userRepository.save(user);
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(id))
        );
        return userMapper.userToUserDto(user);
    }


    @Override
    public List<UserDto> findAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::userToUserDto).collect(Collectors.toList());
    }

}