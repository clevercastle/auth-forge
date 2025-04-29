package org.clevercastle.helper.login.examples.springboot.springbootexample;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserLoginItem;
import org.clevercastle.helper.login.UserRegisterRequest;
import org.clevercastle.helper.login.UserService;
import org.clevercastle.helper.login.UserWithToken;
import org.clevercastle.helper.login.oauth2.Oauth2ClientConfig;
import org.clevercastle.helper.login.oauth2.github.GithubOauth2ExchangeService;
import org.clevercastle.helper.login.oauth2.oidc.OidcExchangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Base64;
import java.util.List;

@RestController
public class AuthController {
    private static final Oauth2ClientConfig googleClientConfig = Oauth2ClientConfig.builder()
            .uniqueId("google")
            .oauth2ExchangeService(new OidcExchangeService())
            .clientId("")
            .clientSecret("")
            .oauth2LoginUrl("https://accounts.google.com/o/oauth2/v2/auth")
            .oauth2TokenUrl("https://oauth2.googleapis.com/token")
            .scopes(List.of("openid", "profile", "email"))
            .emailFunction((map) -> {
                Object email = map.get("email");
                if (email instanceof String) {
                    if (StringUtils.isNotBlank((String) email)) {
                        Object emailVerified = map.get("email_verified");
                        if (emailVerified instanceof Boolean && !((boolean) emailVerified)) {
                            return null;
                        }
                        return (String) email;
                    }
                    return null;
                }
                return null;
            })
            .nameFunction((map) -> {
                Object name = map.get("name");
                if (name instanceof String) {
                    return (String) name;
                }
                return null;
            })
            .build();

    private static final Oauth2ClientConfig githubClientConfig = Oauth2ClientConfig.builder()
            .uniqueId("github")
            .clientId("")
            .clientSecret("")
            .oauth2ExchangeService(new GithubOauth2ExchangeService())
            .oauth2LoginUrl("https://github.com/login/oauth/authorize")
            .oauth2TokenUrl("https://github.com/login/oauth/access_token")
            .scopes(List.of("user:email"))
            .httpClient(new HttpClientImpl())
            .build();


    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("auth/register")
    public User register(@RequestBody RegisterRequest request) throws CastleException {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setLoginIdentifier("email#" + request.getEmail());
        userRegisterRequest.setPassword(request.getPassword());
        userRegisterRequest.setLoginIdentifierPrefix("email");
        return userService.register(userRegisterRequest);
    }

    @GetMapping("auth/verify")
    public UserWithToken verify(@RequestParam String loginIdentifier, @RequestParam String verificationCode) throws CastleException {
        userService.verify("email#" + loginIdentifier, verificationCode);
        return null;
    }

    @GetMapping("auth/login")
    public UserWithToken login(@RequestHeader String authorization) throws CastleException {
        // decode basic authentication
        authorization = authorization.replace("Basic ", "");
        // base64 decode
        String[] credentials = new String(Base64.getDecoder().decode(authorization)).split(":");
        String loginIdentifier = "email#" + credentials[0];
        String password = credentials[1];
        return userService.login(loginIdentifier, password);
    }

    @GetMapping("auth/refresh")
    public UserWithToken login(@RequestHeader String authorization, @RequestBody RefreshTokenRequest refreshToken) throws CastleException, ParseException {
        // decode basic authentication
        authorization = authorization.replace("Bearer ", "");
        // TODO: 2025/4/29 verify the token
        DecodedJWT jwt = JWT.decode(authorization);
        final String userSub = jwt.getSubject();
        if (StringUtils.isBlank(userSub)) {
            throw new CastleException("<UNK>");
        }
        userService.getByLoginIdentifier(userSub);
        Pair<User, UserLoginItem> pair = userService.getByUserSub(userSub);
        if (pair.getLeft() == null || pair.getRight() == null) {
            throw new CastleException("<UNK>");
        }
        return userService.refresh(pair.getLeft(), pair.getRight(), refreshToken.getRefreshToken());
    }

    @GetMapping("auth/sso/url")
    public String generateUrl(@RequestParam SsoType ssoType, @RequestParam String redirectUrl) {
        // decode basic authentication
        switch (ssoType) {
            case google:
                return userService.generate(googleClientConfig, redirectUrl);
            case github:
                return userService.generate(githubClientConfig, redirectUrl);
        }
        return null;
    }

    @GetMapping("auth/exchange")
    public UserWithToken exchange(@RequestParam SsoType ssoType, @RequestParam String code, @RequestParam String state, @RequestParam String redirectUrl) throws CastleException {
        switch (ssoType) {
            case google:
                return userService.exchange(googleClientConfig, code, state, redirectUrl);
            case github:
                return userService.exchange(githubClientConfig, code, state, redirectUrl);
        }
        return null;
    }
}
