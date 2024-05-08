package com.xiao.users.util;

import com.xiao.users.dto.RoleDto;

public class RoleUtil {
    public static RoleDto buildRoleDto() {
        return RoleDto.builder()
                .id(1L)
                .slug("super-admin")
                .name("Super Admin")
                .description("All permission")
                .build();
    }
}
