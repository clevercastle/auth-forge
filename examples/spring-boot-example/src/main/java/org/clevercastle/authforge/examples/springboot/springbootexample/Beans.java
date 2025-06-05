package org.clevercastle.authforge.examples.springboot.springbootexample;

import com.auth0.jwt.algorithms.Algorithm;
import org.clevercastle.authforge.Config;
import org.clevercastle.authforge.UserService;
import org.clevercastle.authforge.UserServiceImpl;
import org.clevercastle.authforge.repository.UserRepository;
import org.clevercastle.authforge.repository.dynamodb.DynamodbUserRepositoryImpl;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaOneTimePasswordRepository;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserLoginItemRepository;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserModelRepository;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserRefreshTokenMappingRepository;
import org.clevercastle.authforge.repository.rdsjpa.RdsJpaUserRepositoryImpl;
import org.clevercastle.authforge.token.TokenService;
import org.clevercastle.authforge.token.jwt.JwtTokenService;
import org.clevercastle.authforge.code.DummyCodeSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

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
    public UserRepository userRepository(RdsJpaUserModelRepository userModelRepository,
                                         RdsJpaUserLoginItemRepository userLoginItemRepository,
                                         RdsJpaUserRefreshTokenMappingRepository userRefreshTokenMappingRepository,
                                         RdsJpaOneTimePasswordRepository oneTimePasswordRepository) {
        return new RdsJpaUserRepositoryImpl(userModelRepository, userLoginItemRepository,
                userRefreshTokenMappingRepository, oneTimePasswordRepository);
    }


    @Bean
    public UserRepository dynamodbUserRepository(DynamoDbEnhancedClient dynamodbEnhancedClient, DynamoDbClient dynamodbClient) {
        return new DynamodbUserRepositoryImpl(dynamodbEnhancedClient, dynamodbClient);
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
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
        return new JwtTokenService(Config.builder().build(), "client-01", "kid", algorithm);
    }

    @Bean
    public UserService userService(UserRepository userRepository, TokenService tokenService) {
        return new UserServiceImpl(Config.builder().build(), userRepository, tokenService, new DummyCodeSender());
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
