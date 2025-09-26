package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.impl.postgres.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toModel(UserEntity entity);

    UserEntity toEntity(User model);
}