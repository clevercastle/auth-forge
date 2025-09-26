package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import com.auth0.jwt.algorithms.Algorithm;
import org.clevercastle.authforge.core.CacheService;
import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.DummyCacheServiceImpl;
import org.clevercastle.authforge.core.code.CodeSender;
import org.clevercastle.authforge.core.code.DummyCodeSender;
import org.clevercastle.authforge.core.repository.OneTimePasswordRepository;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.core.service.OtpService;
import org.clevercastle.authforge.core.service.TokenSessionService;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.service.impl.OtpServiceImpl;
import org.clevercastle.authforge.core.service.impl.TokenSessionServiceImpl;
import org.clevercastle.authforge.core.service.impl.UserAuthServiceImpl;
import org.clevercastle.authforge.core.token.TokenService;
import org.clevercastle.authforge.core.token.jwt.JwtTokenService;
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

    // PostgreSQL repository implementations are auto-detected by Spring Boot via @Repository annotations
    // No need to manually create beans - Spring will auto-wire them

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
    public Config config() {
        return Config.builder().build();
    }

    @Bean
    public CodeSender codeSender() {
        return new DummyCodeSender();
    }

    @Bean
    public CacheService cacheService() {
        return new DummyCacheServiceImpl();
    }

    @Bean
    public UserAuthService userAuthService(Config config,
                                           UserRepository userModelRepository,
                                           UserLoginItemRepository loginItemRepository,
                                           RefreshTokenRepository refreshTokenRepository,
                                           TokenService tokenService,
                                           CodeSender codeSender,
                                           CacheService cacheService) {
        return new UserAuthServiceImpl(config, userModelRepository, loginItemRepository,
                refreshTokenRepository, tokenService, codeSender, cacheService);
    }

    @Bean
    public OtpService otpService(Config config,
                                 OneTimePasswordRepository oneTimePasswordRepository,
                                 TokenService tokenService,
                                 CodeSender codeSender,
                                 RefreshTokenRepository refreshTokenRepository,
                                 UserAuthService userAuthService) {
        return new OtpServiceImpl(config, oneTimePasswordRepository, tokenService,
                codeSender, refreshTokenRepository, userAuthService);
    }

    @Bean
    public TokenSessionService tokenSessionService(RefreshTokenRepository refreshTokenRepository,
                                                   TokenService tokenService) {
        return new TokenSessionServiceImpl(refreshTokenRepository, tokenService);
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