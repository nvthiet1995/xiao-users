package com.xiao.users.controller;

import com.xiao.users.constants.UserConstants;
import com.xiao.users.dto.ResponseDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.service.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService iUserService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody UserDto userDto) {
        iUserService.createUser(userDto);
        logger.info("Create user successfully!");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(UserConstants.STATUS_201, UserConstants.MESSAGE_201));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable("id") Long userId) {
        UserDto userDto = iUserService.findUserById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<UserDto>> fetchAllUsers() {
        List<UserDto> userDtos = iUserService.fetchAllUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtos);
    }

}
