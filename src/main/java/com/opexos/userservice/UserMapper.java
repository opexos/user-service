package com.opexos.userservice;

import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public void fill(@NonNull User entity, @NonNull UserEditDTO dto) {
        entity.setUsername(dto.getUsername())
                .setEmail(dto.getEmail())
                .setFullName(dto.getFullName())
                .setBirthday(dto.getBirthday())
                .setSex(dto.getSex());
    }

    public UserDTO toDTO(@NonNull User entity) {
        return UserDTO.builder()
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .birthday(entity.getBirthday())
                .sex(entity.getSex())
                .build();
    }

    public UserDTO toDTO(@NonNull UserListProjection entity) {
        return UserDTO.builder()
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .build();
    }

}
