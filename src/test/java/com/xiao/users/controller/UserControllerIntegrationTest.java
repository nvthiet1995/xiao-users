package com.xiao.users.controller;

import com.xiao.users.dto.UserDto;
import com.xiao.users.entity.User;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.util.JsonUtil;
import com.xiao.users.util.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;
import java.util.List;

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

    private String userAuthentication;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUpAuthentication(){
        UserDto userDto = UserUtil.buildUserDto();
        User userInsert = userMapper.userDtoToUser(userDto);
        userInsert.setUsername("user_auth");
        userInsert.setEmailAddress("user_auth@gmail.com");
        userInsert.setPassword(passwordEncoder.encode(userInsert.getPassword()));

        userRepository.save(userInsert);
        String credentials = userInsert.getEmailAddress() + ":" + userDto.getPassword();
        userAuthentication = new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

    @Test
    void testCreateAccount_201() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userDto))
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(415));
    }

    @Test
    void testFindUserById_200() throws Exception {

        UserDto userDto = UserUtil.buildUserDto();
        User user = userRepository.save(userMapper.userDtoToUser(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void testFindUserById_404() throws Exception {
        Long userId = 999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value(String.format("User not found with the given input data id : '%s'", userId)));
    }

    @Test
    void testFindAllUser_200() throws Exception {
        userRepository.deleteAll();
        UserDto userDto1 = UserUtil.buildUserDto();
        UserDto userDto2 = UserUtil.buildUserDto();
        userDto2.setEmailAddress("abc2@gmail.com");

        User user1 = userMapper.userDtoToUser(userDto1);
        user1.setPassword(passwordEncoder.encode(userDto1.getPassword()));
        User user2 = userMapper.userDtoToUser(userDto2);
        user2.setPassword(passwordEncoder.encode(userDto2.getPassword()));

        userRepository.save(user1);
        userRepository.save(user2);

        String credentials = userDto1.getEmailAddress() + ":" + userDto1.getPassword();
        userAuthentication = new String(Base64.getEncoder().encode(credentials.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("pages", "0").param("pageSize", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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
    void testFindAllUser_200_withPageSizeIs1() throws Exception {
        userRepository.deleteAll();
        UserDto userDto1 = UserUtil.buildUserDto();
        UserDto userDto2 = UserUtil.buildUserDto();
        userDto2.setEmailAddress("abc2@gmail.com");

        User user1 = userMapper.userDtoToUser(userDto1);
        user1.setPassword(passwordEncoder.encode(userDto1.getPassword()));
        User user2 = userMapper.userDtoToUser(userDto2);
        user2.setPassword(passwordEncoder.encode(userDto2.getPassword()));

        User userPage1 = userRepository.save(user1);
        User userPage2 = userRepository.save(user2);

        String credentials = userDto1.getEmailAddress() + ":" + userDto1.getPassword();
        userAuthentication = new String(Base64.getEncoder().encode(credentials.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("pages", "0").param("pageSize", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(1)))
                .andExpect(jsonPath("$.content[0].username", is(userPage1.getUsername())))
                .andExpect(jsonPath("$.content[0].password", is(userPage1.getPassword())))
                .andExpect(jsonPath("$.content[0].emailAddress", is(userPage1.getEmailAddress())));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("pages", "1").param("pageSize", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(1)))
                .andExpect(jsonPath("$.content[0].username", is(userPage2.getUsername())))
                .andExpect(jsonPath("$.content[0].password", is(userPage2.getPassword())))
                .andExpect(jsonPath("$.content[0].emailAddress", is(userPage2.getEmailAddress())));
    }

    @Test
    void testFindAllUser_whenMissingParams() throws Exception {
        userRepository.deleteAll();
        UserDto userDto1 = UserUtil.buildUserDto();
        UserDto userDto2 = UserUtil.buildUserDto();
        userDto2.setEmailAddress("abc2@gmail.com");

        User user1 = userMapper.userDtoToUser(userDto1);
        user1.setPassword(passwordEncoder.encode(userDto1.getPassword()));
        User user2 = userMapper.userDtoToUser(userDto2);
        user2.setPassword(passwordEncoder.encode(userDto2.getPassword()));

        userRepository.save(user1);
        userRepository.save(user2);

        String credentials = userDto1.getEmailAddress() + ":" + userDto1.getPassword();
        userAuthentication = new String(Base64.getEncoder().encode(credentials.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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

    /*** cannot be empty when a user is required for authentication
    @Test
    void testFindAllUser_whenEmptyUserList() throws Exception {
        userRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)));

    }
     **/

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
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
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
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value(String.format("User not found with the given input data id : '%s'", userId)));
    }

    @Test
    void testAuthentication_whenEmptyHeader() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();
        User user = userRepository.save(userMapper.userDtoToUser(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthentication_whenPasswordIsWrong() throws Exception {
        userRepository.deleteAll();
        UserDto userDto = UserUtil.buildUserDto();
        User user = userRepository.save(userMapper.userDtoToUser(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + userAuthentication))
                .andExpect(status().isUnauthorized());
    }
}