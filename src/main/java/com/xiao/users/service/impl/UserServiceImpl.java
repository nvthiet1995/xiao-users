package com.xiao.users.service.impl;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import com.xiao.users.exception.DuplicateUserException;
import com.xiao.users.exception.ResourceNotFoundException;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

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
    public Page<UserDto> findAllUser(int pages, int pageSize) {
        Page<User> usersPage = userRepository.findAll(PageRequest.of(pages, pageSize));
        return usersPage.map(userMapper::userToUserDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto){
        if(userRepository.findById(userId).isEmpty()){
            throw new ResourceNotFoundException("User", "id", String.valueOf(userId));
        }

        if(isExistEmailOrUserName(userId, userDto)){
            throw new DuplicateUserException(userDto.getUsername(), userDto.getEmailAddress());
        }

        User userDataUpdate = userMapper.userDtoToUser(userDto);
        userDataUpdate.setId(userId);
        return userMapper.userToUserDto(userRepository.save(userDataUpdate));
    }

    private boolean isExistEmailOrUserName(Long userId ,UserDto userDto){
        Optional<User> userOptional = userRepository.findUserByEmailOrUserNameWithId(userId, userDto.getUsername(), userDto.getEmailAddress());
        return userOptional.isPresent();
    }
}