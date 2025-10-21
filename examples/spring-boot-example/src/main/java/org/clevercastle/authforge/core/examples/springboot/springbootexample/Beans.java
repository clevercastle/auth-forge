package org.clevercastle.authforge.core.examples.springboot.springbootexample;

import org.clevercastle.authforge.core.Config;
import org.clevercastle.authforge.core.DummyCacheServiceImpl;
import org.clevercastle.authforge.core.codesender.CodeSender;
import org.clevercastle.authforge.core.codesender.DummyCodeSender;
import org.clevercastle.authforge.core.repository.OneTimePasswordRepository;
import org.clevercastle.authforge.core.repository.RefreshTokenRepository;
import org.clevercastle.authforge.core.repository.UserLoginItemRepository;
import org.clevercastle.authforge.core.repository.UserRepository;
import org.clevercastle.authforge.core.repository.VerificationCodeRepository;
import org.clevercastle.authforge.core.service.CacheService;
import org.clevercastle.authforge.core.service.OtpService;
import org.clevercastle.authforge.core.service.TokenManager;
import org.clevercastle.authforge.core.service.UserAuthService;
import org.clevercastle.authforge.core.service.VerificationCodeService;
import org.clevercastle.authforge.core.service.impl.OtpServiceImpl;
import org.clevercastle.authforge.core.service.impl.TokenManagerImpl;
import org.clevercastle.authforge.core.service.impl.UserAuthServiceImpl;
import org.clevercastle.authforge.core.service.impl.VerificationCodeServiceImpl;
import org.clevercastle.authforge.core.token.SignatureProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class Beans {

    // PostgreSQL repository implementations are auto-detected by Spring Boot via @Repository annotations
    // No need to manually create beans - Spring will auto-wire them

//    @Bean
//    public TokenGenerator tokenService() throws NoSuchAlgorithmException, InvalidKeySpecException {
//        String privateKeyBase64 = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg9dIFmLwqXyr9fLX8XYOL5tiS63YJP0NGo9+7wqm3gdahRANCAATcI/NjILO7b1x7CQwHkB2+CGsrIKqI94fh8aEtaWTIzGYn1vct9u2/AvORtn6qBpi4/rJH4XxFekFigifbXors";
//        byte[] publicKeyBytes = Base64.getDecoder().decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE3CPzYyCzu29cewkMB5AdvghrKyCqiPeH4fGhLWlkyMxmJ9b3LfbtvwLzkbZ+qgaYuP6yR+F8RXpBYoIn216K7A==");
//        KeyFactory keyFactory = KeyFactory.getInstance("EC");
//
//        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
//
//        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
//        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
//        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
//
//        // Validate the key type and curve
//        var algorithm = Algorithm.ECDSA256((ECPublicKey) publicKey, (ECPrivateKey) privateKey);
//        return new TokenGenerator(Config.builder().build(), "client-01", "kid", algorithm);
//    }

    @Bean
    public SignatureProvider signatureProvider() {
        return new SignatureProvider() {
            @Override
            public byte[] sign(byte[] data) {
                return new byte[0];
            }

            @Override
            public boolean verify(byte[] data, byte[] signature) {
                return false;
            }

            @Override
            public String keyId() {
                return "";
            }

            @Override
            public String alg() {
                return "";
            }
        };
    }

    @Bean
    public Config config() {
        return Config.builder()
                .verificationCodeExpireTime(60)
                .oneTimePasswordExpireTime(60)
                .tokenExpireTime(3600)
                .build();
    }

    @Bean
    public CodeSender codeSender() {
        return new DummyCodeSender();
    }

    @Bean
    public CacheService cacheService() {
        return new DummyCacheServiceImpl();
    }

    @Lazy
    @Bean
    public VerificationCodeService verificationCodeService(@Lazy UserAuthService userAuthService,
                                                           VerificationCodeRepository verificationCodeRepository) {
        return new VerificationCodeServiceImpl(userAuthService, verificationCodeRepository);
    }

    @Lazy
    @Bean
    public UserAuthService userAuthService(Config config,
                                           UserRepository userModelRepository,
                                           UserLoginItemRepository loginItemRepository,
                                           RefreshTokenRepository refreshTokenRepository,
                                           TokenManager tokenManager,
                                           VerificationCodeService verificationCodeService,
                                           CodeSender codeSender,
                                           CacheService cacheService) {
        return new UserAuthServiceImpl(config, userModelRepository, loginItemRepository,
                refreshTokenRepository, tokenManager, codeSender, verificationCodeService, cacheService);
    }

    @Bean
    public OtpService otpService(Config config,
                                 OneTimePasswordRepository oneTimePasswordRepository,
                                 TokenManager tokenManager,
                                 CodeSender codeSender,
                                 RefreshTokenRepository refreshTokenRepository,
                                 UserAuthService userAuthService) {
        return new OtpServiceImpl(config, oneTimePasswordRepository, tokenManager,
                codeSender, refreshTokenRepository, userAuthService);
    }

    @Bean
    public TokenManager tokenSessionService(Config config,
                                            SignatureProvider signatureProvider,
                                            RefreshTokenRepository refreshTokenRepository) {
        return new TokenManagerImpl(config, List.of(signatureProvider), refreshTokenRepository);
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