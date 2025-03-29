package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.clevercastle.helper.login.User;
import org.clevercastle.helper.login.UserService;
import org.clevercastle.helper.login.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
}
