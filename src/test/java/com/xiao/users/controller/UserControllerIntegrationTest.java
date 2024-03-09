package com.xiao.users.controller;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.util.JsonUtil;
import com.xiao.users.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestParam;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Test
    void testCreateAccount_201() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("User created successfully"));
    }

    @Test
    void testCreateAccount_400() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();
        userDto.setUsername(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("Missing username"));
    }

    @Test
    void testCreateAccount_415() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_CBOR)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(415));
    }

    @Test
    void testFindUserById_200() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        User user = userRepository.save(userMapper.userDtoToUser(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void testFindUserById_404() throws Exception {
        Long userId = 999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value(String.format("User not found with the given input data id : '%s'", userId)));
    }

    @Test
    void testFindAllUser_200() throws Exception {
        UserDto userDto1 = UserUtil.buildUserDto();
        UserDto userDto2 = UserUtil.buildUserDto();

        User user1 = userRepository.save(userMapper.userDtoToUser(userDto1));
        User user2 = userRepository.save(userMapper.userDtoToUser(userDto2));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("pages", "0").param("pageSize", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)))
                .andExpect(jsonPath("$.content[0].username", is(user1.getUsername())))
                .andExpect(jsonPath("$.content[0].password", is(user1.getPassword())))
                .andExpect(jsonPath("$.content[0].emailAddress", is(user1.getEmailAddress())))
                .andExpect(jsonPath("$.content[1].username", is(user2.getUsername())))
                .andExpect(jsonPath("$.content[1].emailAddress", is(user2.getEmailAddress())))
                .andExpect(jsonPath("$.content[1].password", is(user2.getPassword())));
    }

    @Test
    void testFindAllUser_whenMissingParams() throws Exception {
        UserDto userDto1 = UserUtil.buildUserDto();
        UserDto userDto2 = UserUtil.buildUserDto();

        User user1 = userRepository.save(userMapper.userDtoToUser(userDto1));
        User user2 = userRepository.save(userMapper.userDtoToUser(userDto2));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)))
                .andExpect(jsonPath("$.content[0].username", is(user1.getUsername())))
                .andExpect(jsonPath("$.content[0].password", is(user1.getPassword())))
                .andExpect(jsonPath("$.content[0].emailAddress", is(user1.getEmailAddress())))
                .andExpect(jsonPath("$.content[1].username", is(user2.getUsername())))
                .andExpect(jsonPath("$.content[1].emailAddress", is(user2.getEmailAddress())))
                .andExpect(jsonPath("$.content[1].password", is(user2.getPassword())));
    }

    @Test
    void testFindAllUser_whenEmptyUserList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)));

    }

    @Test
    void testUpdateUser_201() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserDto userUpdate = UserUtil.buildUserDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userSaved.getId()))
                .andExpect(jsonPath("$.username").value(userUpdate.getUsername()))
                .andExpect(jsonPath("$.password").value(userUpdate.getPassword()))
                .andExpect(jsonPath("$.emailAddress").value(userUpdate.getEmailAddress()));
    }

    @Test
    void testUpdateUser_400() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserDto userUpdate = UserUtil.buildUserDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");
        userUpdate.setEmailAddress("test_gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.emailAddress").value("Email is not valid"));
    }

    @Test
    void testUpdateUser_415() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserDto userUpdate = UserUtil.buildUserDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");
        userUpdate.setEmailAddress("test_gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                .contentType(MediaType.APPLICATION_CBOR)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(415));
    }

    @Test
    void testUpdateUser_whenNotFoundUserId() throws Exception {
        Long userId = 999L;
        UserDto userUpdate = UserUtil.buildUserDto();
        userUpdate.setUsername(userUpdate.getUsername()+"_updated");
        userUpdate.setPassword(userUpdate.getPassword()+"_updated");
        userUpdate.setEmailAddress(userUpdate.getEmailAddress()+"_updated");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value(String.format("User not found with the given input data id : '%s'", userId)));
    }
}