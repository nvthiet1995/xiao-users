package com.xiao.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponseDto {

    private String message;

    private HttpStatus code;

    private String title;

    private Map<String, String> errors;

    private Object data;

}