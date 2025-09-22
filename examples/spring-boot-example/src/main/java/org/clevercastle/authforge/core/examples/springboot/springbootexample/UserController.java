package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.UserService;
import org.clevercastle.authforge.core.exception.CastleException;
import org.clevercastle.authforge.core.model.User;
import org.clevercastle.authforge.core.totp.RequestTotpResponse;
import org.clevercastle.authforge.core.totp.SetupTotpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("user/mfa/request")
    public RequestTotpResponse requestTotp() throws CastleException {
        User user = new User();
        return userService.requestTotp(user);
    }

    @PostMapping("user/mfa/verify")
    public void verifyTotp(@RequestBody SetupTotpRequest request) throws CastleException {
        User user = new User();
        user.setUserId("user-01");
        userService.setupTotp(user, request);
    }
}
