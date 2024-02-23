package com.xiao.users.controller;

import com.xiao.users.dto.ResponseDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.service.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final IUserService iUserService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody UserDto userDto) {
        iUserService.createUser(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201", "OK"));
    }

    @GetMapping("/fetchAll")
    public ResponseEntity<List<UserDto>> fetchAllUsers() {
        List<UserDto> userDtos = iUserService.fetchAllAccount();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtos);
    }

}
