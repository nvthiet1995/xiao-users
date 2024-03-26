package com.xiao.users.service.impl;

import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.User;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = UserUtil.buildUserDto();

        userService.createUser(userDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_whenUserRepositoryGotException() {
        UserDto userDto = UserUtil.buildUserDto();
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("exception"));

        try {
            userService.createUser(userDto);
            fail("Should throw exception");
        } catch (RuntimeException ex) {
            assertEquals(ex.getMessage(), "exception");
        }

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindUserById() {
        Long userId = 1L;
        User user = UserUtil.buildUser();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.findUserById(userId);

        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getEmailAddress(), result.getEmailAddress());
    }

    @Test
    void testFindUserById_whenNotFound() {
        Long userId = 1L;
        User user = UserUtil.buildUser();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try {
            UserDto result = userService.findUserById(userId);
            fail("Should throw exception");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), String.format("%s not found with the given input data %s : '%s'", "User", "id", userId));
        }
    }

    @Test
    void testFindAllUser(){

        List<User> userList = Arrays.asList(UserUtil.buildUser(), UserUtil.buildUser());
        Page<User> expectedPage = new PageImpl<>(userList, PageRequest.of(0, 10), userList.size());
        when(userRepository.findAll(PageRequest.of(0,10))).thenReturn(expectedPage);

        Page<UserDto> actualPage = userService.findAllUser(0, 10);
        Page<UserDto> expectedResult = expectedPage.map(userMapper::userToUserDto);

        assertEquals(expectedResult.getContent(), actualPage.getContent());
        assertEquals(expectedResult.getSize(), actualPage.getSize());
        assertEquals(expectedResult.getNumber(), actualPage.getNumber());
        assertEquals(expectedResult.getTotalPages(), actualPage.getTotalPages());
        assertEquals(expectedResult.getTotalElements(), actualPage.getTotalElements());
        assertEquals(expectedResult.hasNext(), actualPage.hasNext());
        assertEquals(expectedResult.hasPrevious(), actualPage.hasPrevious());

        verify(userRepository, times(1)).findAll(PageRequest.of(0,10));
    }

    @Test
    void testFindAllUser_whenUserRepositoryGotException(){
        when(userRepository.findAll(PageRequest.of(0,10))).thenThrow(new RuntimeException("exception"));

        try {
            userService.findAllUser(0,10);
            fail("Should throw exception");
        } catch (RuntimeException ex) {
            assertEquals(ex.getMessage(), "exception");
        }

        verify(userRepository, times(1)).findAll(PageRequest.of(0,10));
    }


    @Test
    void testUpdateUser() {
        Long userIdToUpdate = 2L;
        User existingUser = UserUtil.buildUser();
        existingUser.setId(userIdToUpdate);

        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");
        userUpdate.setId(userIdToUpdate);

        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(userMapper.userUpdateDtoToUser(userUpdate));

        UserDto actualUser = userService.updateUser(userIdToUpdate, userUpdate);
        assertEquals(actualUser.getId(), userUpdate.getId());
        assertEquals(actualUser.getPassword(), userUpdate.getPassword());
        assertEquals(actualUser.getUsername(), userUpdate.getUsername());
        assertEquals(actualUser.getEmailAddress(), userUpdate.getEmailAddress());

        verify(userRepository, times(1)).findById(userIdToUpdate);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_whenNotFound() {
        Long userIdToUpdate = 2L;

        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");
        userUpdate.setId(userIdToUpdate);

        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.empty());

        try {
            UserDto userResult = userService.updateUser(userIdToUpdate, userUpdate);
            fail("Should throw exception");
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), String.format("%s not found with the given input data %s : '%s'", "User", "id", userIdToUpdate));
        }
    }

    @Test
    void testUpdateUser_whenUserRepositoryGotException() {
        Long userIdToUpdate = 2L;
        User existingUser = UserUtil.buildUser();
        existingUser.setId(userIdToUpdate);

        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");
        userUpdate.setId(userIdToUpdate);

        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("exception"));

        try {
            UserDto userResult = userService.updateUser(userIdToUpdate, userUpdate);
            fail("Should throw exception");
        } catch (RuntimeException ex) {
            assertEquals(ex.getMessage(), "exception");
        }

        verify(userRepository, times(1)).findById(userIdToUpdate);
        verify(userRepository, times(1)).save(any(User.class));
    }

}