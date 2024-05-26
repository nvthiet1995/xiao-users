package com.xiao.users.mapper;

import com.xiao.users.dto.RoleDto;
import com.xiao.users.entity.Role;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role roleDtoToRole(RoleDto roleDto);

    Set<Role> roleDtosToRoles(Set<RoleDto> roleDtoSet);

    RoleDto roleToRoleDto(Role role);
}
