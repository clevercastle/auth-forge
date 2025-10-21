package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.verificationcode.VerificationCode;
import org.clevercastle.authforge.impl.postgres.entity.VerificationCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerificationCodeMapper {

    VerificationCodeMapper INSTANCE = Mappers.getMapper(VerificationCodeMapper.class);

    VerificationCode toModel(VerificationCodeEntity entity);

    VerificationCodeEntity toEntity(VerificationCode model);
}
