package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.clevercastle.helper.login.CastleException;
import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserRegisterRequest;
import org.clevercastle.helper.login.UserService;
import org.clevercastle.helper.login.UserWithToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class AuthController {
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
}
