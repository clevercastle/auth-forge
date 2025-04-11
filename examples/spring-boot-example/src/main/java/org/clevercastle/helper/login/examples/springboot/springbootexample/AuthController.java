package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.apache.commons.lang3.StringUtils;
import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.User;
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
        userRegisterRequest.setLoginIdentifier(request.getEmail());
        userRegisterRequest.setPassword(request.getPassword());
        return userService.register(userRegisterRequest);
    }

    @GetMapping("auth/login")
    public UserWithToken login(@RequestHeader String authorization) throws CastleException {
        // decode basic authentication
        authorization = authorization.replace("Basic ", "");
        // base64 decode
        String[] credentials = new String(Base64.getDecoder().decode(authorization)).split(":");
        String loginIdentifier = credentials[0];
        String password = credentials[1];
        return userService.login(loginIdentifier, password);
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
