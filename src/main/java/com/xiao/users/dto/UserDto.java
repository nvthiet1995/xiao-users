package com.xiao.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.Set;

@Data
public class UserDto {

    private Long id;

    @Setter(AccessLevel.NONE)
    @NotEmpty(message = "Missing username")
    private String username;

    @NotEmpty(message = "Missing password")
    @Size(min = 8, message = "Password length must be greater then 8")
    private String password;

    @Email(message = "Email is not valid")
    private String emailAddress;

    private Set<RoleDto> roles;

    public UserDto(String username, String password, String emailAddress, Set<RoleDto> roles) {
        this.setUsername(username);
        this.password = password;
        this.emailAddress = emailAddress;
        this.roles = roles;
    }

    public void setUsername(String username) {
        this.username = (username != null && !username.trim().isEmpty()) ? username.trim() : null;
    }
}