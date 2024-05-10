package com.xiao.users.util;

import com.xiao.users.dto.RoleDto;
import com.xiao.users.entity.Role;

public class RoleUtil {
    public static RoleDto buildRoleDto() {
        return RoleDto.builder()
                .id(1L)
                .slug("super-admin")
                .name("Super Admin")
                .description("All permission")
                .build();
    }

    public static Role buildRole() {
        return Role.builder()
                .id(1L)
                .slug("super-admin")
                .name("Super Admin")
                .description("All permission")
                .build();
    }
}
