package org.clevercastle.authforge.impl.postgres.repository.jpa;

import org.clevercastle.authforge.core.user.UserLoginItem;
import org.clevercastle.authforge.impl.postgres.entity.UserEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemEntity;
import org.clevercastle.authforge.impl.postgres.entity.UserLoginItemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserLoginItemJpaRepository using Testcontainers PostgreSQL.
 *
 * Test Strategy:
 * - Uses real PostgreSQL database via Testcontainers
 * - Tests composite primary key (loginIdentifierType, loginIdentifier)
 * - Validates uniqueness constraints
 * - Tests all repository methods
 */
@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/db/user-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("UserLoginItemJpaRepository Integration Tests")
@EntityScan(basePackageClasses = {UserEntity.class})
@EnableJpaRepositories(basePackageClasses = {UserJpaRepository.class})
class UserLoginItemJpaRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.entityManagerFactoryInterface", () -> "jakarta.persistence.EntityManagerFactory");
    }

    @Autowired
    private UserLoginItemJpaRepository repository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity testUser;
    private String testUserId;

    @BeforeEach
    void setUp() {
        // Create a test user for foreign key relationship
        testUserId = "user-" + UUID.randomUUID();
        testUser = new UserEntity();
        testUser.setUserId(testUserId);
        testUser.setState(org.clevercastle.authforge.core.user.UserState.active);
        testUser.setHashedPassword("$2a$12$hashedPassword");
        testUser.setCreatedAt(OffsetDateTime.now());
        testUser.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(testUser);
        entityManager.flush();
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {

        @Test
        @DisplayName("Should save UserLoginItem with email authentication")
        void shouldSaveEmailLoginItem() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);

            // When
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // Then
            UserLoginItemId id = new UserLoginItemId("email", "alice@example.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);

            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifierType()).isEqualTo("email");
            assertThat(found.get().getLoginIdentifier()).isEqualTo("alice@example.com");
            assertThat(found.get().getType()).isEqualTo(UserLoginItem.Type.raw);
            assertThat(found.get().getUserSub()).isEqualTo(entity.getUserSub());
            assertThat(found.get().getUserId()).isEqualTo(testUserId);
            assertThat(found.get().getState()).isEqualTo(UserLoginItem.State.unconfirmed);
            assertThat(found.get().getCreatedAt()).isNotNull();
            assertThat(found.get().getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should save UserLoginItem with Google SSO")
        void shouldSaveGoogleSsoLoginItem() {
            // Given
            UserLoginItemEntity entity = createLoginItem("google", "alice@gmail.com", UserLoginItem.Type.sso);
            entity.setSsoSub("105612345678901234567"); // Google sub claim
            entity.setState(UserLoginItem.State.active); // SSO items are immediately active

            // When
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // Then
            UserLoginItemId id = new UserLoginItemId("google", "alice@gmail.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);

            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifierType()).isEqualTo("google");
            assertThat(found.get().getSsoSub()).isEqualTo("105612345678901234567");
            assertThat(found.get().getType()).isEqualTo(UserLoginItem.Type.sso);
            assertThat(found.get().getState()).isEqualTo(UserLoginItem.State.active);
        }

        @Test
        @DisplayName("Should save UserLoginItem with Enterprise SSO")
        void shouldSaveEnterpriseSsoLoginItem() {
            // Given
            UserLoginItemEntity entity = createLoginItem("azure_entra_acme", "bob@acmecorp.com",
                    UserLoginItem.Type.enterprise_sso);
            entity.setSsoSub("azure-ad-object-id-12345");
            entity.setState(UserLoginItem.State.active);

            // When
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // Then
            UserLoginItemId id = new UserLoginItemId("azure_entra_acme", "bob@acmecorp.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);

            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifierType()).isEqualTo("azure_entra_acme");
            assertThat(found.get().getType()).isEqualTo(UserLoginItem.Type.enterprise_sso);
            assertThat(found.get().getSsoSub()).isEqualTo("azure-ad-object-id-12345");
        }

        @Test
        @DisplayName("Should allow same email with different loginIdentifierType")
        void shouldAllowSameEmailDifferentTypes() {
            // Given - Same email, different types
            UserLoginItemEntity emailLogin = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            UserLoginItemEntity googleLogin = createLoginItem("google", "alice@example.com", UserLoginItem.Type.sso);

            // When
            repository.save(emailLogin);
            repository.save(googleLogin);
            entityManager.flush();
            entityManager.clear();

            // Then - Both should exist
            UserLoginItemId emailId = new UserLoginItemId("email", "alice@example.com");
            UserLoginItemId googleId = new UserLoginItemId("google", "alice@example.com");

            assertThat(repository.findById(emailId)).isPresent();
            assertThat(repository.findById(googleId)).isPresent();
            assertThat(repository.count()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find by composite primary key")
        void shouldFindByCompositePrimaryKey() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When
            UserLoginItemId id = new UserLoginItemId("email", "alice@example.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifierType()).isEqualTo("email");
            assertThat(found.get().getLoginIdentifier()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("Should find by userSub")
        void shouldFindByUserSub() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            String userSub = "email:unique-sub-123";
            entity.setUserSub(userSub);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When
            Optional<UserLoginItemEntity> found = repository.findByUserSub(userSub);

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getUserSub()).isEqualTo(userSub);
            assertThat(found.get().getLoginIdentifier()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("Should find by loginIdentifierType and loginIdentifier")
        void shouldFindByLoginIdentifierTypeAndLoginIdentifier() {
            // Given
            UserLoginItemEntity entity = createLoginItem("github", "alice", UserLoginItem.Type.sso);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When
            Optional<UserLoginItemEntity> found = repository.findByLoginIdentifierAndLoginIdentifierType("alice", "github");

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifierType()).isEqualTo("github");
            assertThat(found.get().getLoginIdentifier()).isEqualTo("alice");
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            // When
            UserLoginItemId id = new UserLoginItemId("email", "nonexistent@example.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should distinguish between same identifier different types")
        void shouldDistinguishSameIdentifierDifferentTypes() {
            // Given
            String email = "alice@example.com";
            UserLoginItemEntity emailLogin = createLoginItem("email", email, UserLoginItem.Type.raw);
            UserLoginItemEntity googleLogin = createLoginItem("google", email, UserLoginItem.Type.sso);
            repository.save(emailLogin);
            repository.save(googleLogin);
            entityManager.flush();
            entityManager.clear();

            // When
            Optional<UserLoginItemEntity> foundEmail = repository.findByLoginIdentifierAndLoginIdentifierType(email, "email");
            Optional<UserLoginItemEntity> foundGoogle = repository.findByLoginIdentifierAndLoginIdentifierType(email, "google");

            // Then
            assertThat(foundEmail).isPresent();
            assertThat(foundGoogle).isPresent();
            assertThat(foundEmail.get().getLoginIdentifierType()).isEqualTo("email");
            assertThat(foundGoogle.get().getLoginIdentifierType()).isEqualTo("google");
            assertThat(foundEmail.get().getType()).isEqualTo(UserLoginItem.Type.raw);
            assertThat(foundGoogle.get().getType()).isEqualTo(UserLoginItem.Type.sso);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should confirm login item by userSub")
        void shouldConfirmLoginItemByUserSub() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            String userSub = "email:confirm-test-123";
            entity.setUserSub(userSub);
            entity.setState(UserLoginItem.State.unconfirmed);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When
            int updated = repository.confirmLoginItem(userSub);
            entityManager.flush();
            entityManager.clear();

            // Then
            assertThat(updated).isEqualTo(1);

            Optional<UserLoginItemEntity> confirmed = repository.findByUserSub(userSub);
            assertThat(confirmed).isPresent();
            assertThat(confirmed.get().getState()).isEqualTo(UserLoginItem.State.active);
        }

        @Test
        @DisplayName("Should update existing login item")
        void shouldUpdateExistingLoginItem() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When - Update state
            UserLoginItemId id = new UserLoginItemId("email", "alice@example.com");
            UserLoginItemEntity found = repository.findById(id).orElseThrow();
            found.setState(UserLoginItem.State.active);
            repository.save(found);
            entityManager.flush();
            entityManager.clear();

            // Then
            UserLoginItemEntity updated = repository.findById(id).orElseThrow();
            assertThat(updated.getState()).isEqualTo(UserLoginItem.State.active);
            assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete by composite primary key")
        void shouldDeleteByCompositePrimaryKey() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When
            UserLoginItemId id = new UserLoginItemId("email", "alice@example.com");
            repository.deleteById(id);
            entityManager.flush();
            entityManager.clear();

            // Then
            Optional<UserLoginItemEntity> found = repository.findById(id);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should cascade delete when user is deleted")
        void shouldCascadeDeleteWhenUserDeleted() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // When - Delete parent user
            userRepository.deleteById(testUserId);
            entityManager.flush();
            entityManager.clear();

            // Then - Login item should be deleted due to CASCADE
            UserLoginItemId id = new UserLoginItemId("email", "alice@example.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Constraint Validation")
    class ConstraintValidation {

        @Test
        @DisplayName("Should enforce userSub uniqueness")
        void shouldEnforceUserSubUniqueness() {
            // Given
            String sharedUserSub = "duplicate-user-sub-123";
            UserLoginItemEntity entity1 = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            entity1.setUserSub(sharedUserSub);

            UserLoginItemEntity entity2 = createLoginItem("google", "bob@gmail.com", UserLoginItem.Type.sso);
            entity2.setUserSub(sharedUserSub);

            // When & Then
            repository.save(entity1);
            entityManager.flush();

            // Second save should fail due to unique constraint on userSub
            org.junit.jupiter.api.Assertions.assertThrows(
                    Exception.class,
                    () -> {
                        repository.save(entity2);
                        entityManager.flush();
                    }
            );
        }

        @Test
        @DisplayName("Should enforce composite primary key uniqueness")
        void shouldEnforceCompositePrimaryKeyUniqueness() {
            // Given
            UserLoginItemEntity entity1 = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            UserLoginItemEntity entity2 = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);

            // When & Then
            repository.save(entity1);
            entityManager.flush();

            // Second save should fail due to primary key constraint
            org.junit.jupiter.api.Assertions.assertThrows(
                    Exception.class,
                    () -> {
                        repository.save(entity2);
                        entityManager.flush();
                    }
            );
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null ssoSub for raw type")
        void shouldHandleNullSsoSubForRawType() {
            // Given
            UserLoginItemEntity entity = createLoginItem("email", "alice@example.com", UserLoginItem.Type.raw);
            entity.setSsoSub(null); // Raw type doesn't need ssoSub

            // When
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // Then
            UserLoginItemId id = new UserLoginItemId("email", "alice@example.com");
            Optional<UserLoginItemEntity> found = repository.findById(id);

            assertThat(found).isPresent();
            assertThat(found.get().getSsoSub()).isNull();
        }

        @Test
        @DisplayName("Should handle long identifier strings")
        void shouldHandleLongIdentifierStrings() {
            // Given - Maximum length identifier
            String longIdentifier = "very.long.email.address.with.many.characters@example.com.with.subdomain.and.more";
            UserLoginItemEntity entity = createLoginItem("email", longIdentifier, UserLoginItem.Type.raw);

            // When
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // Then
            UserLoginItemId id = new UserLoginItemId("email", longIdentifier);
            Optional<UserLoginItemEntity> found = repository.findById(id);

            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifier()).isEqualTo(longIdentifier);
        }

        @Test
        @DisplayName("Should handle special characters in identifiers")
        void shouldHandleSpecialCharactersInIdentifiers() {
            // Given
            String specialEmail = "user+tag@example.com";
            UserLoginItemEntity entity = createLoginItem("email", specialEmail, UserLoginItem.Type.raw);

            // When
            repository.save(entity);
            entityManager.flush();
            entityManager.clear();

            // Then
            Optional<UserLoginItemEntity> found = repository.findByLoginIdentifierAndLoginIdentifierType(specialEmail, "email");

            assertThat(found).isPresent();
            assertThat(found.get().getLoginIdentifier()).isEqualTo(specialEmail);
        }
    }

    // Helper Methods

    private UserLoginItemEntity createLoginItem(String type, String identifier, UserLoginItem.Type loginType) {
        UserLoginItemEntity entity = new UserLoginItemEntity();
        entity.setLoginIdentifierType(type);
        entity.setLoginIdentifier(identifier);
        entity.setType(loginType);
        entity.setUserSub(type + ":" + UUID.randomUUID());
        entity.setUserId(testUserId);
        entity.setState(UserLoginItem.State.unconfirmed);
        return entity;
    }
}
