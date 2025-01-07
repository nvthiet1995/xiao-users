package com.xiao.users.controller;

import com.xiao.users.constants.UserConstants;
import com.xiao.users.dto.ResponseDto;
import com.xiao.users.dto.UserDto;
import com.xiao.users.dto.UserUpdateDto;
import com.xiao.users.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final IUserService iUserService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }

    @Operation(
            summary = "Create a new user",
            description = "Create an account with a role to use in the XIAO system",
            tags = {"user", "post"}
    )
    @PostMapping
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody UserDto userDto) {
        iUserService.createUser(userDto);
        logger.info("Create user successfully!");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(UserConstants.STATUS_201, UserConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Get a user",
            description = "Get a user by Id",
            tags = {"user", "get"}
    )
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> findUserById(
            @Parameter(description = "ID of the item to be obtained", required = true)
            @PathVariable("id") Long userId) {
        UserDto userDto = iUserService.findUserById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @Operation(
            summary = "Get a user",
            description = "Get a user by Id",
            tags = {"user", "get"}
    )
    @GetMapping
    public ResponseEntity<Page<UserDto>> findAllUser(
            @Parameter(description = "The page requires redirection.")
            @RequestParam(defaultValue = "0") int pages,
            @Parameter(description = "The number of elements on the page.")
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<UserDto> userDtos = iUserService.findAllUser(pages, pageSize);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtos);
    }

    @Operation(
            summary = "Edit a user",
            description = "Edit a user by Id",
            tags = {"user", "put"}
    )
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID of the item to be obtained", required = true)
            @PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto){
        UserDto userResponse = iUserService.updateUser(id, userUpdateDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userResponse);
    }

    @Operation(
            summary = "Delete a user",
            description = "Delete a user by Id",
            tags = {"user", "delete"}
    )
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the item to be obtained", required = true)
            @PathVariable @Pattern(regexp = "\\d+", message = "User ID must be numeric and not empty") String id){
        iUserService.deleteUser(Long.parseLong(id));
        return ResponseEntity.ok().build();
    }
}
