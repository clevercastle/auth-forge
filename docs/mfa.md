# MFA Flow

## TOTP Setup Flow

```mermaid
flowchart TD
    A[User requests TOTP setup] --> B[Generate secret key]
    B --> C[Store secret in cache with session ID]
    C --> D[Generate QR code URI]
    D --> E[Return session ID, secret, and QR code to user]
    E --> F[User scans QR code with authenticator app]
    F --> G[User enters verification code from app]
    G --> H[Client sends session ID and verification code]
    H --> I[Server retrieves secret from cache using session ID]
    I --> J[Server verifies TOTP code using secret]
    J --> K{Verification successful?}
    K -- Yes --> L[Save secret to database]
    K -- No --> M[Return error]
    L --> N[Clear cache]
    N --> O[TOTP setup complete]
    M --> P[User can retry with new code]
```

## MFA Challenge Flow

```mermaid
flowchart TD
    A[User login with username/password] --> B{User has MFA enabled?}
    B -- No --> C[Login successful]
    B -- Yes --> D[Create MFA challenge]
    D --> E[Store challenge session with expiration]
    E --> F[Return challenge ID to client]
    F --> G[User opens authenticator app]
    G --> H[User enters TOTP code]
    H --> I[Client sends challenge ID and TOTP code]
    I --> J[Server validates challenge session]
    J --> K{Challenge valid and not expired?}
    K -- No --> L[Return challenge error]
    K -- Yes --> M[Retrieve user's TOTP secret]
    M --> N[Verify TOTP code against secret]
    N --> O{TOTP code valid?}
    O -- No --> P[Return verification error]
    O -- Yes --> Q[Clear challenge session]
    Q --> R[Complete login process]
    R --> S[Return access token]
```

## MFA Factor Management Flow

```mermaid
flowchart TD
    A[User requests MFA factors] --> B[Retrieve user's MFA factors from database]
    B --> C[Return list of factors with metadata]
    C --> D[User views factors list]
    D --> E{User action?}
    E -- Add TOTP --> F[Start TOTP setup flow]
    E -- Delete factor --> G[Delete factor from database]
    E -- Test factor --> H[Create test challenge]
    F --> I[TOTP setup flow]
    G --> J[Update factors list]
    H --> K[MFA challenge flow]
```

## Security Considerations

### Time Window Tolerance

- TOTP codes are valid for ±1 time window (30 seconds each)
- Prevents clock drift issues between server and client

### Challenge Session Management

- Challenge sessions expire after 5 minutes
- Prevents replay attacks and session hijacking

### Secret Key Security

- Secrets generated using SecureRandom
- Temporary secrets stored in cache with expiration
- Persistent secrets encrypted in database

### Rate Limiting (Recommended)

- Limit TOTP verification attempts
- Implement account lockout after failed attempts
- Add delays between verification attempts

# Mfa Challenge Flow

```mermaid
flowchart TD
    A[User login] --> B{Authenticated?}
    B -- No --> Z[Fail to login]
    B -- Yes --> C{Server: decide whether need challenge}
    C -- Yes --> D[Server: Return the challenge to the client, response should include all possible challenge response solutions]
    C -- No --> Y[Server: Return the token to the user]
    D --> E[Customer choose on solution and answer the challenge]
    E --> F{Server: is the response correct}
    F -- No --> E
    F -- Yes --> Y

```