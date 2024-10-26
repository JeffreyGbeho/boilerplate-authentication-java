package com.selfengineerjourney.auth.utils.mapper;

import com.selfengineerjourney.auth.dto.UserDto;
import com.selfengineerjourney.auth.entity.Role;
import com.selfengineerjourney.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserDto toDto(User user);

    @Named("rolesToStrings")
    default List<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toList());
    }
}
