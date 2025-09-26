package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.model.UserLoginItem;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserLoginItemMapper {

    UserLoginItemMapper INSTANCE = Mappers.getMapper(UserLoginItemMapper.class);

    UserLoginItem toModel(UserLoginItemEntity entity);

    UserLoginItemEntity toEntity(UserLoginItem model);
}