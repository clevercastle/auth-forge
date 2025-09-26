package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import jakarta.persistence.EntityManager;
import org.clevercastle.authforge.core.repository.ChallengeSessionRepository;
import org.clevercastle.authforge.core.repository.OneTimePasswordRepository;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.repository.UserHmacSecretRepository;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.repository.UserRegistrationRepository;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresChallengeSessionRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresLoginItemRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresOneTimePasswordRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresRefreshTokenRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresUserHmacSecretRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresUserModelRepository;
import org.clevercastle.authforge.impl.postgres.repository.PostgresUserRegistrationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

@Configuration
public class RepositoryBeans {

    @Bean
    public UserRepository userModelRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresUserModelRepository(jpaRepositoryFactory);
    }

    @Bean
    public UserLoginItemRepository loginItemRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresLoginItemRepository(jpaRepositoryFactory);
    }

    @Bean
    public UserRegistrationRepository userRegistrationRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresUserRegistrationRepository(jpaRepositoryFactory);
    }

    @Bean
    public RefreshTokenRepository refreshTokenRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresRefreshTokenRepository(jpaRepositoryFactory);
    }

    @Bean
    public OneTimePasswordRepository oneTimePasswordRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresOneTimePasswordRepository(jpaRepositoryFactory);
    }

    @Bean
    public UserHmacSecretRepository userHmacSecretRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresUserHmacSecretRepository(jpaRepositoryFactory);
    }

    @Bean
    public ChallengeSessionRepository challengeSessionRepository(EntityManager entityManager) {
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        return new PostgresChallengeSessionRepository(jpaRepositoryFactory);
    }
}