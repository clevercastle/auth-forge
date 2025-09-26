package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.model.UserHmacSecret;
import org.clevercastle.authforge.impl.postgres.entity.UserHmacSecretEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserHmacSecretMapper {

    UserHmacSecretMapper INSTANCE = Mappers.getMapper(UserHmacSecretMapper.class);

    UserHmacSecret toModel(UserHmacSecretEntity entity);

    UserHmacSecretEntity toEntity(UserHmacSecret model);

    List<UserHmacSecret> toModelList(List<UserHmacSecretEntity> entities);
}