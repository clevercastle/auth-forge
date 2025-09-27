package org.clevercastle.authforge.impl.postgres.mapper;

import org.clevercastle.authforge.core.model.ChallengeSession;
import org.clevercastle.authforge.impl.postgres.entity.ChallengeSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChallengeSessionMapper {

    ChallengeSessionMapper INSTANCE = Mappers.getMapper(ChallengeSessionMapper.class);

    ChallengeSession toModel(ChallengeSessionEntity entity);

    ChallengeSessionEntity toEntity(ChallengeSession model);
}