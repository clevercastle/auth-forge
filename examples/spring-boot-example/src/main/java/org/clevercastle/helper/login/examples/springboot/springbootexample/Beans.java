package org.clevercastle.helper.login.examples.springboot.springbootexample;

import org.clevercastle.helper.login.UserService;
import org.clevercastle.helper.login.UserServiceImpl;
import org.clevercastle.helper.login.repository.UserRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserLoginItemRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserModelRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
    @Bean
    public UserRepository userRepository(UserModelRepository userModelRepository,
                                         UserLoginItemRepository userLoginItemRepository) {
        return new UserRepositoryImpl(userModelRepository, userLoginItemRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }
}
