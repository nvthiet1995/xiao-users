package com.xiao.users.controller;

import com.xiao.users.constants.UserConstants;
import com.xiao.users.dto.ResponseDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.exception.EmptyAllFieldsUpdateException;
import com.xiao.users.service.IUserService;
import com.xiao.users.validator.FieldValueEmpty;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService iUserService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @PostMapping
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

    @GetMapping
    public ResponseEntity<Page<UserDto>> findAllUser(
            @RequestParam(defaultValue = "0") int pages,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<UserDto> userDtos = iUserService.findAllUser(pages, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtos);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                              @FieldValueEmpty(fields = {"username", "password", "emailAddress"})
                                              @RequestBody UserDto userDto,
                                              BindingResult errors
    ){
        if(errors.hasErrors()){
            throw new EmptyAllFieldsUpdateException();
        }
        UserDto userResponse = iUserService.updateUser(id, userDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponse);
    }
}
