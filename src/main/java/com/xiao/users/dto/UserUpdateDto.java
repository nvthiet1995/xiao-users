package com.xiao.users.dto;

import com.xiao.users.validator.CheckAllValueEmpty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@CheckAllValueEmpty(fields = {"username", "password", "emailAddress"})
public class UserUpdateDto {

    private Long id;
    private String username;

    @Size(min = 8, max = 16, message = "Password length must be between 8 and 16 characters")
    private String password;

    @Email(message = "Email is not valid", groups = EmailValidation.class)
    private String emailAddress;

    public interface EmailValidation{}
}