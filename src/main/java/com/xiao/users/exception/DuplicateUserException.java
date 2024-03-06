package com.xiao.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateUserException extends RuntimeException{
    public DuplicateUserException(String username, String email){
        super(String.format("Username: %s or email: %s already exists", username, email));
    }
}
