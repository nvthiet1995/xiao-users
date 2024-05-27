package com.xiao.users.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoleDto{
    private Long id;

    @NotEmpty(message = "Missing role name")
    @Size(min = 2, message = "Role name length must be greater then 2")
    private String name;

    private String description;
}
