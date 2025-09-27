package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.model.UserRefreshTokenMapping;
import org.clevercastle.authforge.impl.postgres.entity.UserRefreshTokenMappingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRefreshTokenMappingMapper {

    UserRefreshTokenMappingMapper INSTANCE = Mappers.getMapper(UserRefreshTokenMappingMapper.class);

    UserRefreshTokenMapping toModel(UserRefreshTokenMappingEntity entity);

    UserRefreshTokenMappingEntity toEntity(UserRefreshTokenMapping model);
}