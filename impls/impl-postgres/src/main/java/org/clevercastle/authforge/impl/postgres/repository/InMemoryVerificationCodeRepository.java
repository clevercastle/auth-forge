package org.clevercastle.authforge.impl.postgres.repository;

import org.clevercastle.authforge.core.repository.VerificationCodeRepository;
import org.clevercastle.authforge.core.verificationcode.VerificationCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVerificationCodeRepository implements VerificationCodeRepository {
    public static final Map<String, VerificationCode> verificationCodeStore = new ConcurrentHashMap<>();

    @Override
    public void save(VerificationCode verificationCode) {
        verificationCodeStore.put(verificationCode.getCode(), verificationCode);
    }

    @Override
    public VerificationCode getByCode(String code) {
        return verificationCodeStore.get(code);
    }

    @Override
    public void markCodeAsUsed(String code) {
        verificationCodeStore.remove(code);
    }
}
