package com.xiao.users.controller;

import com.xiao.users.dto.RoleDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.entity.Role;
import com.xiao.users.entity.User;
import com.xiao.users.mapper.RoleMapper;
import com.xiao.users.mapper.UserMapper;
import com.xiao.users.repository.RoleRepository;
import com.xiao.users.repository.UserRepository;
import com.xiao.users.util.JsonUtil;
import com.xiao.users.util.RoleUtil;
import com.xiao.users.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void tearDown(){
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
    @Test
    @WithMockUser
    void testCreateAccount_201() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("User created successfully"));
    }

    @Test
    @WithMockUser
    void testCreateAccount_201_whenSetRoles() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();
        Set<RoleDto> roleDtoSet = new HashSet<>();
        roleDtoSet.add(RoleUtil.buildRoleDto());
        userDto.setRoles(roleDtoSet);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("User created successfully"));
    }

    @Test
    @WithMockUser
    void testCreateAccount_400() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();
        userDto.setUsername(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Your Method Argument Is Not Valid"))
                .andExpect(jsonPath("$.title").value("VALIDATION ERROR"))
                .andExpect(jsonPath("$.errors.username").value("Missing username"));
    }

    @Test
    @WithMockUser
    void testCreateAccount_415() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_CBOR)
                        .content(JsonUtil.asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(415));
    }

    @Test
    @WithMockUser
    void testFindUserById_200() throws Exception {
        UserDto userDto = UserUtil.buildUserDto();

        User user = userRepository.save(userMapper.userDtoToUser(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", user.getId())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    @WithMockUser
    void testFindUserById_404() throws Exception {
        Long userId = 999L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.title").value("Resource Not Found Error"))
                .andExpect(jsonPath("$.message").value(String.format("User not found with the given input data id : '%s'", userId)));
    }

    @Test
    @WithMockUser
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
                .andExpect(jsonPath("$.content[0].emailAddress", is(user1.getEmailAddress())))
                .andExpect(jsonPath("$.content[1].username", is(user2.getUsername())))
                .andExpect(jsonPath("$.content[1].emailAddress", is(user2.getEmailAddress())));
    }

    @Test
    @WithMockUser
    void testFindAllUser_200_withPageSizeIs1() throws Exception {

        User userPage1 = userRepository.save(UserUtil.buildUser());
        User userPage2 = userRepository.save(UserUtil.buildUser());

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("pages", "0").param("pageSize", "1")
                        .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(1)))
                .andExpect(jsonPath("$.content[0].username", is(userPage1.getUsername())))
                .andExpect(jsonPath("$.content[0].emailAddress", is(userPage1.getEmailAddress())));

        mockMvc.perform(MockMvcRequestBuilders.get("/users").param("pages", "1").param("pageSize", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(1)))
                .andExpect(jsonPath("$.pageable.pageSize", is(1)))
                .andExpect(jsonPath("$.content[0].username", is(userPage2.getUsername())))
                .andExpect(jsonPath("$.content[0].emailAddress", is(userPage2.getEmailAddress())));
    }

    @Test
    @WithMockUser
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
                .andExpect(jsonPath("$.content[0].emailAddress", is(user1.getEmailAddress())))
                .andExpect(jsonPath("$.content[1].username", is(user2.getUsername())))
                .andExpect(jsonPath("$.content[1].emailAddress", is(user2.getEmailAddress())));
    }

    @Test
    @WithMockUser
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
    @WithMockUser
    void testUpdateUser_201() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userSaved.getId()))
                .andExpect(jsonPath("$.username").value(userUpdate.getUsername()))
                .andExpect(jsonPath("$.emailAddress").value(userUpdate.getEmailAddress()));
    }

    @Test
    @WithMockUser
    void testUpdateUser_201_whenSetRoles() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        Role adminRole = RoleUtil.buildRole();
        roleRepository.save(adminRole);
        Set<RoleDto> roleSetDto = new HashSet<>();
        roleSetDto.add(roleMapper.roleToRoleDto(adminRole));

        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setRoles(roleSetDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userUpdate))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userSaved.getId()))
                .andExpect(jsonPath("$.username").value(userUpdate.getUsername()))
                .andExpect(jsonPath("$.emailAddress").value(userUpdate.getEmailAddress()))
                .andExpect(jsonPath("$.roles[0].id").value(adminRole.getId()))
                .andExpect(jsonPath("$.roles[0].name").value(adminRole.getName()));
    }

    @Test
    @WithMockUser
    void testUpdateUser_201_whenEmptyTwoField() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setPassword(null);
        userUpdate.setEmailAddress(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.asJsonString(userUpdate))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userSaved.getId()))
                .andExpect(jsonPath("$.username").value(userUpdate.getUsername()))
                .andExpect(jsonPath("$.emailAddress").value(userSaved.getEmailAddress()));
    }

    @Test
    @WithMockUser
    void testUpdateUser_400_EmptyAllField() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setUsername(null);
        userUpdate.setPassword(null);
        userUpdate.setEmailAddress(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.userUpdateDto").value("Please enter at least one piece when updating the user"));
    }

    @Test
    @WithMockUser
    void testUpdateUser_415() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());
        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();
        userUpdate.setEmailAddress("test@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userSaved.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_CBOR)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(415));
    }

    @Test
    @WithMockUser
    void testUpdateUser_whenNotFoundUserId() throws Exception {
        Long userId = 999L;
        UserUpdateDto userUpdate = UserUtil.buildUserUpdateDto();

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", userId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.asJsonString(userUpdate))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found Error"))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(String.format("User not found with the given input data id : '%s'", userId)));
    }

    @Test
    @WithMockUser
    void testDeleteUser_200() throws Exception {
        User userSaved = userRepository.save(UserUtil.buildUser());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userSaved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser
    void testDeleteUser_whenNotFoundUserId() throws Exception {
        Long userId = 69L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found Error"))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(String.format("User not found with the given input data id : '%s'", userId)));
    }

}