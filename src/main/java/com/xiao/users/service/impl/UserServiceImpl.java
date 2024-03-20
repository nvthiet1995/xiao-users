package com.xiao.users.service.impl;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import com.xiao.users.exception.EmptyAllFieldsUpdateException;
import com.xiao.users.exception.ResourceNotFoundException;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


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
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userId))
        );

        User userDataUpdate = mapValueFieldUpdate(existingUser, userMapper.userDtoToUser(userDto));
        return userMapper.userToUserDto(userRepository.save(userDataUpdate));
    }

    private User mapValueFieldUpdate(User userExist, User userInput) {
        userInput.setId(userExist.getId());

        if(userInput.getUsername().isEmpty()){
            userInput.setUsername(userExist.getUsername());
        }else{
            userInput.setUsername(userInput.getUsername());
        }

        if(userInput.getEmailAddress().isEmpty()){
            userInput.setEmailAddress(userExist.getEmailAddress());
        }else{
            userInput.setEmailAddress(userInput.getEmailAddress());
        }

        if(userInput.getPassword().isEmpty()){
            userInput.setPassword(userExist.getPassword());
        }else{
            userInput.setPassword(userInput.getPassword());
        }
        return userInput;
    }

}