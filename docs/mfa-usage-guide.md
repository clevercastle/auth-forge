# Auth-Forge MFA 功能使用指南

## 概览

Auth-Forge 提供了完整的多因子认证（MFA）功能，兼容 Auth0 的 MFA 接口设计。主要支持以下功能：

- **TOTP (Time-based One-Time Password)**: 基于时间的一次性密码
- **MFA Challenge**: 挑战-响应验证流程
- **MFA Factor Management**: MFA 因子的管理

## 功能特性

### 1. TOTP 支持

- 生成 Base32 编码的密钥
- 支持标准的 30 秒时间窗口
- 提供 QR 码 URI 用于认证器应用
- 支持时间偏移容忍（±1 个时间窗口）

### 2. MFA 挑战流程

- 创建 MFA 挑战会话
- 验证用户提供的 MFA 代码
- 支持挑战会话过期管理

### 3. MFA 因子管理

- 列出用户的所有 MFA 因子
- 删除特定的 MFA 因子
- 查看因子的使用历史

## API 使用示例

### 1. TOTP 设置流程

#### 第一步：请求 TOTP 设置

```java
// 获取 TOTP 设置所需的密钥和 QR 码
RequestTotpResponse response = userService.requestTotp(user);

System.out.println("Session ID: " + response.getSessionId());
System.out.println("Secret: " + response.getSecret());
System.out.println("QR Code URI: " + response.getQrCodeUri());
```

#### 第二步：用户扫描 QR 码并设置认证器

用户需要：

1. 使用认证器应用（如 Google Authenticator、Authy）扫描 QR 码
2. 或者手动输入密钥到认证器应用中
3. 从认证器应用中获取 6 位数字验证码

#### 第三步：完成 TOTP 设置

```java
SetupTotpRequest request = new SetupTotpRequest();
request.setSessionId(response.getSessionId());
request.setName("My Phone Authenticator");

// 添加用户输入的验证码
SetupTotpVerificationCode code = new SetupTotpVerificationCode();
code.setCode("123456"); // 用户从认证器应用获取的代码
code.setInputTime(OffsetDateTime.now());
request.setCodes(Arrays.asList(code));

// 完成设置
userService.setupTotp(user, request);
```

### 2. MFA 挑战验证流程

#### 创建 MFA 挑战

```java
// 为用户创建 TOTP 挑战
MfaChallengeResponse challenge = userService.createMfaChallenge(
    user, "totp", factorId);

System.out.println("Challenge ID: " + challenge.getChallengeId());
System.out.println("Expires at: " + challenge.getExpiresAt());
```

#### 验证 MFA 挑战

```java
// 用户输入认证器应用中的代码进行验证
boolean verified = userService.verifyMfaChallenge(
    challengeId, "654321", null);

if (verified) {
    System.out.println("MFA verification successful!");
} else {
    System.out.println("MFA verification failed!");
}
```

### 3. 直接 TOTP 验证（简化流程）

```java
// 直接验证用户的 TOTP 代码，无需创建挑战
boolean verified = userService.verifyTotpCode(userId, "123456");
```

### 4. MFA 因子管理

#### 列出用户的 MFA 因子

```java
List<MfaFactorResponse> factors = userService.listMfaFactors(userId);

for (MfaFactorResponse factor : factors) {
    System.out.println("Factor: " + factor.getName() + 
                      " (" + factor.getType() + ")");
    System.out.println("Created: " + factor.getCreatedAt());
    System.out.println("Last used: " + factor.getLastUsedAt());
}
```

#### 删除 MFA 因子

```java
userService.deleteMfaFactor(userId, factorId);
```

## 与 Auth0 MFA 的兼容性

本实现参考了 Auth0 的 MFA API 设计，主要兼容以下 Auth0 功能：

### 1. TOTP 因子管理

- 类似于 Auth0 的 `/api/v2/users/{id}/authenticators` 接口
- 支持 TOTP 因子的创建、列表和删除

### 2. MFA 挑战流程

- 类似于 Auth0 的 MFA Challenge API
- 支持挑战的创建和验证

### 3. QR 码生成

- 生成标准的 `otpauth://` URI
- 兼容主流认证器应用

## 安全考虑

### 1. 密钥安全

- 使用 `SecureRandom` 生成密钥
- 密钥采用 Base32 编码存储
- 临时密钥使用缓存存储并设置过期时间

### 2. 时间同步

- 支持 ±1 个时间窗口的容忍度
- 防止时钟偏移导致的验证失败

### 3. 重放攻击防护

- 可以扩展添加使用过的 TOTP 代码记录
- 挑战会话具有过期时间

### 4. 暴力破解防护

- 可以添加验证失败次数限制
- 可以添加账户锁定机制

## 配置选项

### 时间窗口配置

```java
// 在 TotpUtil 中可以调整以下参数：
private static final int TIME_STEP = 30; // 时间步长（秒）
private static final int TIME_WINDOW = 1; // 允许的时间窗口偏移
```

### 挑战会话过期时间

```java
// 在 createMfaChallenge 方法中可以调整：
cacheService.set("mfa_challenge_" + challengeId, factorId, 300); // 5分钟过期
```

## 扩展功能建议

### 1. 备用代码支持

可以扩展支持一次性备用代码，类似于 Auth0 的 Recovery Codes。

### 2. SMS OOB 支持

可以扩展支持短信验证，参考 Auth0 的 OOB (Out-of-Band) 验证。

### 3. 邮件 OOB 支持

可以扩展支持邮件验证。

### 4. 推送通知

可以集成推送通知服务，类似于 Auth0 Guardian。

## 错误处理

### 常见错误及处理

```java
try {
    userService.setupTotp(user, request);
} catch (CastleException e) {
    if (e.getMessage().contains("Invalid verification code")) {
        // 处理验证码错误
    } else if (e.getMessage().contains("session expired")) {
        // 处理会话过期
    } else if (e.getMessage().contains("already configured")) {
        // 处理重复配置
    }
}
```

## 测试建议

### 1. 单元测试

- 测试 TOTP 生成和验证逻辑
- 测试时间窗口容忍度
- 测试密钥生成的随机性

### 2. 集成测试

- 测试完整的 MFA 设置流程
- 测试挑战验证流程
- 测试因子管理功能

### 3. 安全测试

- 测试重放攻击防护
- 测试暴力破解防护
- 测试会话过期处理

这个 MFA 实现为你的认证系统提供了强大的多因子认证功能，同时保持了与 Auth0 接口的兼容性，便于未来的迁移或集成。
