package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.model.OneTimePassword;
import org.clevercastle.authforge.impl.postgres.entity.OneTimePasswordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OneTimePasswordMapper {

    OneTimePasswordMapper INSTANCE = Mappers.getMapper(OneTimePasswordMapper.class);

    OneTimePassword toModel(OneTimePasswordEntity entity);

    OneTimePasswordEntity toEntity(OneTimePassword model);
}