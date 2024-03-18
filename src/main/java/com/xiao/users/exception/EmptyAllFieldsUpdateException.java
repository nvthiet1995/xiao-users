package com.xiao.users.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyAllFieldsUpdateException extends RuntimeException{
    private final HttpStatus httpStatus;
    public EmptyAllFieldsUpdateException(){
        super("Enter at least 1 piece of information");
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
