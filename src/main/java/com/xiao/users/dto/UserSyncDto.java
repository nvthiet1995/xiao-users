package com.xiao.users.dto;

import lombok.Data;

@Data
public class UserSyncDto {

    private Long id;

    private String username;

    private String password;

    private String emailAddress;

}
