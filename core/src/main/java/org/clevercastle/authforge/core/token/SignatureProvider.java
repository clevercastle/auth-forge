package org.clevercastle.authforge.core.token;

public interface SignatureProvider {
    byte[] sign(byte[] data);

    boolean verify(byte[] data, byte[] signature);

    String keyId();

    String alg();
}
