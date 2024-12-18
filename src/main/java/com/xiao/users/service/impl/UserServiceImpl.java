package com.xiao.users.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiao.users.kafka.KafkaConfigProperties;
import com.xiao.users.constants.UserConstants;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;
import com.xiao.users.exception.ResourceNotFoundException;
import com.xiao.users.mapper.RoleMapper;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.service.UserService;
import com.xiao.users.service.UserSyncService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final UserSyncService userSyncService;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            RoleMapper roleMapper,
            UserSyncService userSyncService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userSyncService = userSyncService;
    }

    @Override
    public void createUser(UserDto userDto) throws JsonProcessingException {
        User user = userMapper.userDtoToUser(userDto);
        User createdUser = userRepository.save(user);
        userSyncService.syncUserToAuthServer(createdUser, UserConstants.ActionType.CREATE);
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
    public UserDto updateUser(Long userId, UserUpdateDto userDto) throws JsonProcessingException {
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(userId))
        );

        existingUser = mapValueFieldUpdate(existingUser, userDto);
        User updatedUser = userRepository.save(existingUser);
        userSyncService.syncUserToAuthServer(updatedUser, UserConstants.ActionType.UPDATE);
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) throws JsonProcessingException {
        User deleteUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(id))
        );
        userRepository.deleteById(id);
        userSyncService.syncUserToAuthServer(deleteUser, UserConstants.ActionType.DELETE);
    }

    private User mapValueFieldUpdate(User existingUser, UserUpdateDto userDto) {
        return User.builder()
                .id(existingUser.getId())
                .username(Objects.isNull(userDto.getUsername()) || userDto.getUsername().isEmpty() ? existingUser.getUsername() : userDto.getUsername())
                .emailAddress(Objects.isNull(userDto.getEmailAddress()) || userDto.getEmailAddress().isEmpty() ? existingUser.getEmailAddress() : userDto.getEmailAddress())
                .password(Objects.isNull(userDto.getPassword()) || userDto.getPassword().isEmpty() ? existingUser.getPassword() : userDto.getPassword())
                .roles(roleMapper.roleDtosToRoles(userDto.getRoles()))
                .build();
    }

}