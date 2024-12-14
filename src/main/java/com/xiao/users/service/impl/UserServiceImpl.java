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
import com.xiao.users.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final KafkaConfigProperties appConfig;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

  public UserServiceImpl(
      UserRepository userRepository,
      UserMapper userMapper,
      RoleMapper roleMapper,
      KafkaConfigProperties appConfig,
      KafkaTemplate<String, String> kafkaTemplate,
      ObjectMapper objectMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.roleMapper = roleMapper;
    this.appConfig = appConfig;
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public void createUser(UserDto userDto) throws JsonProcessingException {
    User user = userMapper.userDtoToUser(userDto);
    User createdUser = userRepository.save(user);
    kafkaTemplate.send(
        appConfig.getUserSyncTopic(),
        UserConstants.ActionType.CREATE,
        objectMapper.writeValueAsString(userMapper.userToUserSyncDto(createdUser)));
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
        kafkaTemplate.send(
                appConfig.getUserSyncTopic(),
                UserConstants.ActionType.UPDATE,
                objectMapper.writeValueAsString(userMapper.userToUserSyncDto(updatedUser)));
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) throws JsonProcessingException {
        User deleteUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", String.valueOf(id))
        );
        userRepository.deleteById(id);
        kafkaTemplate.send(
                appConfig.getUserSyncTopic(),
                UserConstants.ActionType.DELETE,
                objectMapper.writeValueAsString(userMapper.userToUserSyncDto(deleteUser)));
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