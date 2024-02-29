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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}