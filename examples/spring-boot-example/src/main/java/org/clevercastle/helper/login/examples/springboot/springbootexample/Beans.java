package org.clevercastle.helper.login.examples.springboot.springbootexample;

import com.auth0.jwt.algorithms.Algorithm;
import org.clevercastle.helper.login.Config;
import org.clevercastle.helper.login.UserService;
import org.clevercastle.helper.login.UserServiceImpl;
import org.clevercastle.helper.login.repository.UserRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserLoginItemRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserModelRepository;
import org.clevercastle.helper.login.repository.rdsjpa.UserRepositoryImpl;
import org.clevercastle.helper.login.token.TokenService;
import org.clevercastle.helper.login.token.jwt.JwtTokenService;
import org.clevercastle.helper.login.verification.DummyVerificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class Beans {
    @Bean
    public UserRepository userRepository(UserModelRepository userModelRepository,
                                         UserLoginItemRepository userLoginItemRepository) {
        return new UserRepositoryImpl(userModelRepository, userLoginItemRepository);
    }

    @Bean
    public TokenService tokenService() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyBase64 = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg9dIFmLwqXyr9fLX8XYOL5tiS63YJP0NGo9+7wqm3gdahRANCAATcI/NjILO7b1x7CQwHkB2+CGsrIKqI94fh8aEtaWTIzGYn1vct9u2/AvORtn6qBpi4/rJH4XxFekFigifbXors";
        byte[] publicKeyBytes = Base64.getDecoder().decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE3CPzYyCzu29cewkMB5AdvghrKyCqiPeH4fGhLWlkyMxmJ9b3LfbtvwLzkbZ+qgaYuP6yR+F8RXpBYoIn216K7A==");
        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // Validate the key type and curve
        var algorithm = Algorithm.ECDSA256((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
        return new JwtTokenService(algorithm, "kid");
    }

    @Bean
    public UserService userService(UserRepository userRepository, UserLoginItemRepository userLoginItemRepository, TokenService tokenService) {
        return new UserServiceImpl(Config.builder().build(), userRepository, tokenService, new DummyVerificationService(userLoginItemRepository));
    }


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}
