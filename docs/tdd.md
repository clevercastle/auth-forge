# Auth-Forge Technical Design Document (TDD)

## 1. Architecture Overview

### 1.1 Layer Separation

Auth-Forge follows a **clean architecture** approach with strict separation between business logic and data persistence:

- **Core Layer**: Business logic, domain models, and repository interfaces
- **Implementation Layer**: Database-specific implementations (PostgreSQL, MySQL, etc.)
- **Example Layer**: Framework integration examples (Spring Boot, etc.)

**Key Principle**: Business layer defines repository interfaces; implementation layer provides database-specific
adapters.

```
┌─────────────────────────────────────────┐
│         Application Layer               │
│    (Spring Boot, Framework Integration) │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│          Core Business Layer            │
│  - Domain Models (User, UserLoginItem)  │
│  - Services (UserAuthService, etc.)     │
│  - Repository Interfaces                │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│       Implementation Layer              │
│  - PostgresUserRepository               │
│  - MySQLUserRepository (future)         │
│  - Entity Mappers                       │
└─────────────────────────────────────────┘
```

---

## 2. Domain Models

### 2.1 User

**Core user entity representing authenticated users.**

**Fields**:

- `userId` (String): Unique user identifier (UUID)
- `hashedPassword` (String):
- `resetPasswordCode` (String): Used for reset password
- `resetPasswordCodeExpiredAt`:
- `state` (UserState): User account state (ACTIVE, DISABLED, etc.)
- `createdAt` (OffsetDateTime): Account creation timestamp
- `updatedAt` (OffsetDateTime): Last modification timestamp

**State Machine**:

```
ACTIVE → DISABLED
   ↓
DELETED
```

**Location**: `core/src/main/java/org/clevercastle/authforge/core/user/User.java`

---

### 2.2 UserLoginItem

**Represents a user's authentication method (email, phone, OAuth provider).**

Each authentication method (email/password, Google OAuth, phone number) is stored as a separate login item.

**Fields**:

**Core Identifiers**:

- `loginIdentifier` (String): **User-facing login credential**

    - For `raw` type: Email address or phone number (e.g., `alice@example.com`, `+1234567890`)
    - For `sso` type: User's email from OAuth provider (e.g., `alice@gmail.com` from Google)
    - For `enterprise_sso` type: Enterprise email/username (e.g., `alice@acmecorp.com`)
    - **NOT globally unique** - Same email can exist across different providers

- `loginIdentifierType` (String): **Specific authentication provider/method**
    - For `raw` type: `email`, `phone`
    - For `sso` type: `google`, `github`, `apple`, `facebook`, `microsoft`
    - For `enterprise_sso` type: Tenant-specific identifier (e.g., `azure_entra_acme_corp`, `okta_tech_inc`)
- **Uniqueness**: `(loginIdentifierType, loginIdentifier)` composite key is unique system-wide

- `type` (Type enum): **High-level authentication category**

    - `raw`: Traditional username/password authentication (email+password, phone+password)
    - `sso`: Public OAuth/OIDC providers (Google, GitHub, Apple, Facebook, Microsoft)
    - `enterprise_sso`: Enterprise identity providers (Azure AD, Okta, SAML-based SSO)

- `sso_sub`:

    - For `sso` type: OAuth provider's `sub` claim (e.g., Google: `105612345678901234567`, GitHub: `12345678`)

- `userSub` (String): **Provider's unique user identifier** (critical for OAuth)

    - Auto-generated stable ID (UUID or hash-based)

- `userId` (String): **Reference to parent User entity** - Links this login method to a user account in Auth-Forge

**Verification & State**:

- `state` (State enum): Login item status

    - `UNCONFIRMED`: Awaiting email/phone verification (only applicable for `raw` type)
    - `ACTIVE`: Verified and ready for authentication

- `verificationCode` (String, optional): Temporary code for email/phone verification (only for `raw` type)
- `verificationCodeExpiredAt` (OffsetDateTime, optional): Verification code expiration timestamp

**Timestamps**:

- `createdAt` (OffsetDateTime): Record creation timestamp
- `updatedAt` (OffsetDateTime): Last modification timestamp

**Field Naming Rationale**:

To avoid confusion, here's the distinction between similar-sounding fields:

| Field                 | Purpose                                      | Example Values                                           | Uniqueness                                | Changes?      |
|-----------------------|----------------------------------------------|----------------------------------------------------------|-------------------------------------------|---------------|
| `loginIdentifier`     | **User-facing credential** (what user types) | `alice@example.com`, `+1234567890`                       | ❌ Not unique alone                        | Never change  |
| `loginIdentifierType` | **Specific provider** (which system)         | `email`, `phone`, `google`, `github`, `azure_entra_acme` | ❌ Not unique alone                        | Never changes |
| **Composite Key**     | `(loginIdentifierType, loginIdentifier)`     | `(google, alice@gmail.com)`                              | ✅ **Unique**                              | -             |
| `type`                | **Category** (authentication method class)   | `raw`, `sso`, `enterprise_sso`                           | ❌ Not unique                              | Never changes |
| `userSub`             | **Auth Forge's unique user login ID**        | `xxxxxxx`                                                | ✅ Unique per provider                     | Never changes |
| `userId`              | **Auth-Forge user reference**                | `user-uuid-550e8400`                                     | ❌ Not unique (one user, many login items) | Never changes |

**Key Insight**:

- Same email (`alice@example.com`) can exist in multiple login items with different `loginIdentifierType`
- Uniqueness is guaranteed by the _combination_ of `(loginIdentifierType, loginIdentifier)`

**Example Scenarios**:

**Scenario 1: Email/Password Registration**

```json
{
  "loginIdentifier": "alice@example.com",
  "loginIdentifierType": "email",
  "type": "raw",
  "userSub": "email:7f3a2b1c9e",
  "userId": "user-550e8400",
  "state": "UNCONFIRMED"
}
```

**Scenario 2: Google OAuth Login**

```json
{
  "loginIdentifier": "105612345678901234567",
  "loginIdentifierType": "google",
  "type": "sso",
  "userSub": "google:105612345678901234567",
  "userId": "user-550e8400",
  "state": "ACTIVE"
}
```

**Scenario 3: Enterprise Azure AD SSO**

```json
{
  "loginIdentifier": "bob@acmecorp.com",
  "loginIdentifierType": "azure_entra_acme",
  "type": "enterprise_sso",
  "userSub": "azure_entra_acme:bob@acmecorp.com",
  "userId": "user-7a4b3c2d",
  "state": "ACTIVE"
}
```

**Scenario 4: User with Multiple Login Methods**

A single user can have multiple UserLoginItems:

1. Email + password (`raw` type) → Primary account
2. Google OAuth (`sso` type) → Linked for convenience
3. GitHub OAuth (`sso` type) → Linked for developer workflows

All three UserLoginItems share the same `userId`, allowing the user to login via any method.

**Location**: `core/src/main/java/org/clevercastle/authforge/core/user/UserLoginItem.java`

---

### 2.3 UserHmacSecret (In-Review)

**Stores HMAC secrets for secure token generation.**

**Fields**:

- `userId` (String): Reference to User entity
- `id` (String): Secret identifier
- `secret` (String): Base64-encoded HMAC secret
- `name` (String): Human-readable secret name
- `lastUsedAt` (OffsetDateTime): Last usage timestamp
- `createdAt` (OffsetDateTime): Creation timestamp

**Purpose**:

- JWT signature generation
- Token validation
- Secret rotation support

**Location**: `core/src/main/java/org/clevercastle/authforge/core/model/UserHmacSecret.java`

---

### 2.4 ChallengeSession (In-Review)

**Manages multi-step authentication flows (MFA, password reset, email verification).**

**Fields**:

- `sessionId` (String): Unique session identifier
- `userId` (String): Reference to User entity
- `challengeType` (ChallengeType): Type of challenge
- `challengeData` (String): JSON-encoded challenge state
- `expiresAt` (OffsetDateTime): Session expiration time
- `createdAt` (OffsetDateTime): Creation timestamp

**Challenge Types**:

- `MFA_SETUP`: Initial MFA enrollment
- `MFA_VERIFY`: MFA verification during login
- `PASSWORD_CHANGE`: Password change requirement
- `EMAIL_VERIFICATION`: Email confirmation

**Lifecycle**:

1. Challenge created → SessionId returned to client
2. Client submits solution → Server validates against session
3. Session consumed or expired → Cleaned up

**Location**: `core/src/main/java/org/clevercastle/authforge/core/challenge/ChallengeSession.java`

---

### 2.5 OneTimePassword (In-Review)

**Stores one-time verification codes (email/SMS verification).**

**Fields**:

- `id` (String): Unique identifier
- `code` (String): Verification code (6-digit numeric)
- `loginIdentifier` (String): Target email/phone
- `purpose` (String): Usage purpose (REGISTRATION, PASSWORD_RESET, etc.)
- `expiresAt` (OffsetDateTime): Code expiration time
- `verified` (boolean): Whether code has been used
- `createdAt` (OffsetDateTime): Creation timestamp

**Validation Rules**:

- Code length: 6 digits
- Expiration: 10 minutes (configurable)
- Single-use: Code invalidated after successful verification
- Rate limiting: Max 5 attempts per code

**Location**: Referenced in
`core/src/main/java/org/clevercastle/authforge/core/repository/OneTimePasswordRepository.java`

---

### 2.6 RefreshToken (In-Review)

**Manages long-lived refresh tokens for JWT renewal.**

**Fields**:

- `userId` (String): Reference to User entity
- `tokenId` (String): Unique token identifier
- `expiresAt` (OffsetDateTime): Token expiration time
- `createdAt` (OffsetDateTime): Creation timestamp
- `lastUsedAt` (OffsetDateTime): Last refresh operation
- `revoked` (boolean): Manual revocation flag

**Token Lifecycle**:

1. Generated during successful login
2. Stored with 30-day expiration (configurable)
3. Used to obtain new access tokens
4. Revoked on logout or security events

**Location**: `core/src/main/java/org/clevercastle/authforge/core/token/RefreshToken.java`

---

## 3. Service Layer (In-Review)

### 3.1 UserAuthService

**Core authentication and authorization service.**

**Responsibilities**:

- User registration with email verification
- Multi-method login (email/password, OAuth)
- User retrieval by login identifier or OAuth sub
- OAuth 2.0 authorization URL generation
- OAuth token exchange

**Key Methods**:

```java
User register(UserRegisterRequest request) throws CastleException;

void verify(String loginIdentifier, String verificationCode) throws CastleException;

UserWithToken login(Application application, String loginIdentifier, String password) throws CastleException;

Pair<User, UserLoginItem> getByLoginIdentifier(String loginIdentifier) throws CastleException;

String generate(Oauth2ClientConfig oauth2Client, String redirectUri);

UserWithToken exchange(Application application, Oauth2ClientConfig clientConfig,
                       String authorizationCode, String state, String redirectUrl) throws CastleException;
```

**Location**: `core/src/main/java/org/clevercastle/authforge/core/service/UserAuthService.java`

---

### 3.2 TokenManager

**JWT and refresh token lifecycle management.**

**Responsibilities**:

- Access token generation (short-lived, 15-60 minutes)
- Refresh token generation and validation
- Token revocation (single session or all sessions)
- Token introspection and validation

**Key Methods**:

```java
TokenHolder generateTokens(User user, Application application) throws CastleException;

TokenHolder refreshAccessToken(String refreshToken, Application application) throws CastleException;

void revokeToken(String userId, String tokenId) throws CastleException;

void revokeAllTokens(String userId) throws CastleException;

boolean validateToken(String accessToken) throws CastleException;
```

**Location**: `core/src/main/java/org/clevercastle/authforge/core/service/TokenManager.java`

---

### 3.3 MfaService

**Multi-Factor Authentication management.**

**Responsibilities**:

- TOTP (Time-based One-Time Password) setup
- TOTP verification
- MFA factor enrollment and management
- Backup code generation

**Key Methods**:

```java
SetupTotpResponse setupTotp(String userId, SetupTotpRequest request) throws CastleException;

void verifyTotp(String userId, String code) throws CastleException;

MfaChallengeResponse createMfaChallenge(String userId) throws CastleException;

boolean verifyMfaChallenge(String sessionId, String code) throws CastleException;

List<MfaFactorResponse> listMfaFactors(String userId) throws CastleException;
```

**Supported MFA Methods**:

- **TOTP**: Authenticator apps (Google Authenticator, Authy)
- **SMS**: One-time codes via SMS (future)
- **Email**: One-time codes via email (future)
- **Backup Codes**: Single-use recovery codes (future)

**Location**: `core/src/main/java/org/clevercastle/authforge/core/service/MfaService.java`

---

### 3.4 OtpService

**One-Time Password management for email/SMS verification.**

**Responsibilities**:

- Generate and send verification codes
- Validate OTP codes
- Handle code expiration and rate limiting

**Key Methods**:

```java
SendCodeResponse sendCode(String loginIdentifier, String purpose) throws CastleException;

boolean verifyCode(String loginIdentifier, String code) throws CastleException;
```

**Location**: `core/src/main/java/org/clevercastle/authforge/core/service/OtpService.java`

---

### 3.5 CacheService

**Distributed caching abstraction for session and token storage.**

**Responsibilities**:

- Session data caching
- Token blacklist management
- Rate limiting counters
- Temporary challenge state storage

**Key Methods**:

```java
void set(String key, String value, Duration ttl);

String get(String key);

void delete(String key);

boolean exists(String key);
```

**Implementation Strategy**:

- Development: In-memory cache (DummyCacheServiceImpl)
- Production: Redis, Memcached, or cloud-native caching

**Location**: `core/src/main/java/org/clevercastle/authforge/core/service/CacheService.java`

---

## 4. Repository Layer

### 4.1 Repository Interfaces (Core Layer)

**Design Philosophy**:

- Core layer defines **contracts** (interfaces)
- Implementation layer provides **adapters** (PostgreSQL, MySQL, MongoDB)
- Enables database portability and testing flexibility

**Repository Interfaces**:

#### UserRepository

```java
User save(User user) throws CastleException;

User getByUserId(String userId) throws CastleException;
```

#### UserLoginItemRepository

```java
void confirmLoginItem(String loginIdentifier) throws CastleException;

UserLoginItem save(UserLoginItem item) throws CastleException;

UserLoginItem getByLoginIdentifier(String loginIdentifier) throws CastleException;

UserLoginItem getByUserSub(String userSub) throws CastleException;
```

#### RefreshTokenRepository

```java
RefreshToken save(RefreshToken token) throws CastleException;

RefreshToken getById(String userId, String tokenId) throws CastleException;

void revoke(String userId, String tokenId) throws CastleException;

void revokeAll(String userId) throws CastleException;
```

#### ChallengeSessionRepository

```java
ChallengeSession save(ChallengeSession session) throws CastleException;

ChallengeSession getById(String sessionId) throws CastleException;

void delete(String sessionId) throws CastleException;
```

#### OneTimePasswordRepository

```java
OneTimePassword save(OneTimePassword otp) throws CastleException;

OneTimePassword getByLoginIdentifier(String loginIdentifier, String purpose) throws CastleException;

void markAsVerified(String id) throws CastleException;
```

#### UserHmacSecretRepository

```java
UserHmacSecret save(UserHmacSecret secret) throws CastleException;

List<UserHmacSecret> listByUserId(String userId) throws CastleException;

void updateLastUsedAt(String userId, String secretId) throws CastleException;
```

**Location**: `core/src/main/java/org/clevercastle/authforge/core/repository/`

---

### 4.2 PostgreSQL Implementation

**Package Structure**:

```
impls/impl-postgres/
├── entity/           # JPA entities
├── mapper/           # Entity ↔ Domain model mappers (MapStruct)
├── repository/       # Repository implementations
└── repository/jpa/   # Spring Data JPA repositories
```

**Entity Mapping Pattern**:

1. **Domain Model** (core): `User.java`
2. **JPA Entity** (impl-postgres): `UserEntity.java`
3. **MapStruct Mapper**: `UserMapper.java` (bidirectional conversion)

**Example Mapper**:

```java

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toModel(UserEntity entity);

    UserEntity toEntity(User model);
}
```

**Benefits**:

- JPA annotations isolated to implementation layer
- Core domain models remain framework-agnostic
- Easy to add alternative database implementations

**Location**: `impls/impl-postgres/src/main/java/org/clevercastle/authforge/impl/postgres/`

---

## 5. Security Components

### 5.1 Password Hashing

**Algorithm**: BCrypt with cost factor 12

**Implementation**:

```java
String hashedPassword = HashUtil.hashPassword(plainPassword);
boolean matches = HashUtil.verifyPassword(plainPassword, hashedPassword);
```

**Location**: `core/src/main/java/org/clevercastle/authforge/core/util/HashUtil.java`

---

### 5.2 JWT Token Generation

**Token Structure**:

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user-id",
    "iss": "auth-forge",
    "aud": "application-client-id",
    "exp": 1735689600,
    "iat": 1735686000,
    "userId": "user-id",
    "username": "user@example.com"
  }
}
```

**Security Features**:

- HMAC-SHA256 signature
- Configurable expiration (default: 15 minutes for access, 30 days for refresh)
- User-specific signing keys (rotation support)

**Location**: `core/src/main/java/org/clevercastle/authforge/core/token/jwt/`

---

### 5.3 TOTP (Time-based One-Time Password)

**Algorithm**: RFC 6238 compliant

**Configuration**:

- Time step: 30 seconds
- Code length: 6 digits
- HMAC algorithm: SHA-1
- Time window tolerance: ±1 step (90 seconds total)

**QR Code URI Format**:

```
otpauth://totp/{issuer}:{account}?secret={secret}&issuer={issuer}&algorithm=SHA1&digits=6&period=30
```

**Test Coverage**: Comprehensive unit tests in `TotpUtilTest.java`

**Location**: `core/src/main/java/org/clevercastle/authforge/core/totp/TotpUtil.java`

---

## 6. OAuth 2.0 Integration

### 6.1 Supported Providers

- **Google**: OpenID Connect
- **GitHub**: OAuth 2.0
- **Generic OIDC**: Extensible for any OIDC-compliant provider

### 6.2 OAuth Flow

```
1. Client → Auth-Forge: Request authorization URL
2. Auth-Forge → Client: Return provider authorization URL
3. Client → OAuth Provider: Redirect user for authorization
4. OAuth Provider → Client: Redirect with authorization code
5. Client → Auth-Forge: Exchange code for tokens
6. Auth-Forge → OAuth Provider: Validate code, fetch user info
7. Auth-Forge → Client: Return JWT tokens + user profile
```

### 6.3 Implementation Classes

**Base Classes**:

- `AbstractOauth2ExchangeService`: Common OAuth logic
- `Oauth2ExchangeService`: Provider interface

**Provider Implementations**:

- `GithubOauth2ExchangeService`: GitHub-specific logic
- `OidcExchangeService`: Generic OIDC provider

**User Profile Mapping**:

- `Oauth2User`: Standard user profile
- `GithubUser`: GitHub-specific fields (extended profile)

**Location**: `core/src/main/java/org/clevercastle/authforge/core/oauth2/`

---

## 7. Testing Strategy

### 7.1 Unit Tests

**Coverage Requirements**:

- Utility classes: 100% (crypto, hashing, TOTP)
- Service layer: ≥80%
- Domain logic: ≥90%

**Testing Frameworks**:

- JUnit 5
- Mockito (service layer mocking)
- AssertJ (fluent assertions)

**Example Test Structure**:

```java

@Test
public void testGenerateTOTP() throws Exception {
    // Given
    String secret = "JBSWY3DPEHPK3PXP";
    long timeSeconds = 1234567890L;

    // When
    String totp = TotpUtil.generateTOTP(timeSeconds, secret);

    // Then
    assertNotNull(totp);
    assertEquals(6, totp.length());
    assertTrue(totp.matches("\\d{6}"));
}
```

**Location**: `core/src/test/java/org/clevercastle/authforge/core/`

---

### 7.2 Integration Tests

**Scope**:

- Repository layer with test database (H2/PostgreSQL)
- OAuth flow with mocked provider responses
- End-to-end authentication scenarios

**Test Database**:

- Development: H2 in-memory database
- CI/CD: Testcontainers with PostgreSQL

---

## 8. Configuration Management

### 8.1 Application Configuration

**Key Configuration Classes**:

- `Config`: Global application settings
- `Oauth2ClientConfig`: OAuth provider configurations
- `Application`: Multi-tenant application metadata

**Example OAuth Configuration**:

```java
Oauth2ClientConfig googleConfig = new Oauth2ClientConfig();
googleConfig.

setClientId("google-client-id");
googleConfig.

setClientSecret("google-client-secret");
googleConfig.

setAuthorizationUrl("https://accounts.google.com/o/oauth2/auth");
googleConfig.

setTokenUrl("https://oauth2.googleapis.com/token");
googleConfig.

setUserInfoUrl("https://www.googleapis.com/oauth2/v3/userinfo");
```

---

### 8.2 Environment-Specific Settings

**Development**:

- In-memory caching
- Console code sender (logs OTP codes)
- Relaxed security (longer token expiration)

**Production**:

- Redis caching
- Email/SMS code delivery
- Strict security (short token expiration, rate limiting)

---

## 9. Error Handling

### 9.1 Exception Hierarchy

**Base Exception**: `CastleException`

**Domain-Specific Exceptions**:

- `UserNotFoundException`: User lookup failures
- `UserExistException`: Duplicate user registration
- `UserNotConfirmedException`: Unverified email/phone
- `InvalidTokenException`: Malformed or invalid JWT
- `ExpiredTokenException`: Expired JWT

**Exception Handling Strategy**:

- Services throw domain exceptions
- Controllers map exceptions to HTTP status codes
- Standardized error response format

---

## 10. Deployment Architecture

### 10.1 Module Structure

```
auth-forge/
├── core/                  # Business logic (framework-agnostic)
├── impls/
│   └── impl-postgres/     # PostgreSQL adapter
├── examples/
│   └── spring-boot-example/  # Reference implementation
└── docs/                  # Documentation
```

### 10.2 Dependency Graph

```
Examples (Spring Boot) → Core ← Impl-Postgres
                         ↓
                    Third-party Libs
                    (JWT, BCrypt, HTTP Client)
```

**Principle**: Core has **zero** framework dependencies (Spring, JPA, etc.)

---

## 11. Future Enhancements

### 11.1 Planned Features

-   [ ] WebAuthn / FIDO2 support (passwordless authentication)
-   [ ] SMS-based OTP delivery
-   [ ] Backup code generation for MFA
-   [ ] User session management dashboard
-   [ ] Audit logging and security event tracking
-   [ ] MySQL and MongoDB repository implementations

### 11.2 Performance Optimization

-   [ ] Token introspection caching
-   [ ] Database connection pooling tuning
-   [ ] Async email/SMS sending
-   [ ] Rate limiting with sliding window

---

## 12. References

### 12.1 Standards Compliance

- **JWT**: RFC 7519 (JSON Web Token)
- **TOTP**: RFC 6238 (Time-Based One-Time Password)
- **OAuth 2.0**: RFC 6749
- **OpenID Connect**: OpenID Connect Core 1.0
- **BCrypt**: Provos-Mazières bcrypt algorithm

### 12.2 Security Best Practices

- OWASP Authentication Cheat Sheet
- NIST Digital Identity Guidelines (SP 800-63B)
- OAuth 2.0 Security Best Current Practice (BCP 212)

---

## Document Version

**Version**: 1.0
**Last Updated**: 2025-01-20
**Maintained By**: Auth-Forge Team
