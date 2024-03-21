package com.xiao.users.dto;

import com.xiao.users.validator.FieldValueEmpty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@FieldValueEmpty(fields = {"username", "password", "emailAddress"})
public class UserDto {

    private Long id;

    @NotEmpty(message = "Missing username")
    private String username;

    @NotEmpty(message = "Missing password")
    @Size(min = 8, message = "Password length must be greater then 8")
    private String password;

    @Email(message = "Email is not valid", groups = EmailValidation.class)
    private String emailAddress;

    public interface EmailValidation{}
}