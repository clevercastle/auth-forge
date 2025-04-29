package org.clevercastle.authforge.util;

import org.clevercastle.authforge.CastleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryptoUtilTest {
    private KeyPair keyPair256;
    private KeyPair keyPair384;
    private KeyPair keyPair521;
    private static final String TEST_DATA = "Hello, World!";
    private static final String EMPTY_DATA = "";
    private static final String LARGE_DATA = "A".repeat(10000); // 10KB string

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");

        // Generate different key sizes for testing
        keyPairGenerator.initialize(256);
        keyPair256 = keyPairGenerator.generateKeyPair();

        keyPairGenerator.initialize(384);
        keyPair384 = keyPairGenerator.generateKeyPair();

        keyPairGenerator.initialize(521);
        keyPair521 = keyPairGenerator.generateKeyPair();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SHA256withECDSA",
            "SHA384withECDSA",
            "SHA512withECDSA"
    })
    void testDifferentSignatureAlgorithms(String algorithm) throws CastleException {
        byte[] signature = CryptoUtil.sign(TEST_DATA, keyPair256.getPrivate(), algorithm);
        String hexSignature = new String(Hex.encode(signature));

        boolean isValid = CryptoUtil.verify(TEST_DATA, hexSignature, keyPair256.getPublic(), algorithm);
        assertTrue(isValid);
    }

    @Test
    void testDifferentKeySizes() throws CastleException {
        // Test with P-256
        byte[] sig256 = CryptoUtil.sign(TEST_DATA, keyPair256.getPrivate(), "SHA256withECDSA");
        assertTrue(CryptoUtil.verify(TEST_DATA, new String(Hex.encode(sig256)), keyPair256.getPublic(), "SHA256withECDSA"));

        // Test with P-384
        byte[] sig384 = CryptoUtil.sign(TEST_DATA, keyPair384.getPrivate(), "SHA384withECDSA");
        assertTrue(CryptoUtil.verify(TEST_DATA, new String(Hex.encode(sig384)), keyPair384.getPublic(), "SHA384withECDSA"));

        // Test with P-521
        byte[] sig521 = CryptoUtil.sign(TEST_DATA, keyPair521.getPrivate(), "SHA512withECDSA");
        assertTrue(CryptoUtil.verify(TEST_DATA, new String(Hex.encode(sig521)), keyPair521.getPublic(), "SHA512withECDSA"));
    }

    @Test
    void testEdgeCaseInputs() throws CastleException {
        // Test empty string
        byte[] emptySignature = CryptoUtil.sign(EMPTY_DATA, keyPair256.getPrivate(), "SHA256withECDSA");
        assertTrue(CryptoUtil.verify(EMPTY_DATA, new String(Hex.encode(emptySignature)), keyPair256.getPublic(), "SHA256withECDSA"));

        // Test large data
        byte[] largeSignature = CryptoUtil.sign(LARGE_DATA, keyPair256.getPrivate(), "SHA256withECDSA");
        assertTrue(CryptoUtil.verify(LARGE_DATA, new String(Hex.encode(largeSignature)), keyPair256.getPublic(), "SHA256withECDSA"));
    }

    @Test
    void testSignatureModification() throws CastleException {
        byte[] signature = CryptoUtil.sign(TEST_DATA, keyPair256.getPrivate(), "SHA256withECDSA");

        // Modify one byte in the signature
        byte[] modifiedSignature = signature.clone();
        modifiedSignature[0] = (byte) (modifiedSignature[0] ^ 0xFF);

        assertFalse(CryptoUtil.verify(TEST_DATA,
                new String(Hex.encode(modifiedSignature)),
                keyPair256.getPublic(),
                "SHA256withECDSA"));
    }

    @Test
    void testDerJoseConversionWithDifferentSizes() throws Exception {
        // Test with P-256 (32 bytes)
        testDerJoseConversionForKeySize(keyPair256, 32);

        // Test with P-384 (48 bytes)
        testDerJoseConversionForKeySize(keyPair384, 48);
    }

    private void testDerJoseConversionForKeySize(KeyPair keyPair, int size) throws Exception {
        byte[] derSignature = CryptoUtil.sign(TEST_DATA, keyPair.getPrivate(), "SHA256withECDSA");
        byte[] joseSignature = CryptoUtil.derToJose(derSignature, size);
        assertEquals(size * 2, joseSignature.length);

        byte[] convertedDerSignature = CryptoUtil.joseToDer(joseSignature, size);
        assertTrue(CryptoUtil.verify(TEST_DATA,
                new String(Hex.encode(convertedDerSignature)),
                keyPair.getPublic(),
                "SHA256withECDSA"));
    }

    @Test
    void testJoseToDerWithLeadingZeros() throws Exception {
        // Create a JOSE signature with leading zeros
        byte[] joseSignature = new byte[64]; // For P-256
        Arrays.fill(joseSignature, (byte) 0);
        joseSignature[31] = 1; // Set last byte of r
        joseSignature[63] = 1; // Set last byte of s

        byte[] derSignature = CryptoUtil.joseToDer(joseSignature, 32);
        // Convert back to JOSE
        byte[] convertedJose = CryptoUtil.derToJose(derSignature, 32);
        assertArrayEquals(joseSignature, convertedJose);
    }

    @Test
    void testDerToJoseWithHighBit() throws Exception {
        // Create a DER signature where both r and s have their high bit set
        byte[] derSignature = CryptoUtil.sign(TEST_DATA, keyPair256.getPrivate(), "SHA256withECDSA");
        byte[] joseSignature = CryptoUtil.derToJose(derSignature, 32);

        // Convert back and verify
        byte[] convertedDer = CryptoUtil.joseToDer(joseSignature, 32);
        assertTrue(CryptoUtil.verify(TEST_DATA,
                new String(Hex.encode(convertedDer)),
                keyPair256.getPublic(),
                "SHA256withECDSA"));
    }

    @Test
    void testNullInputs() throws CastleException {
        assertNull(CryptoUtil.sign(null, keyPair256.getPrivate(), "SHA256withECDSA"));

        assertThrows(CastleException.class, () ->
                CryptoUtil.sign(TEST_DATA, null, "SHA256withECDSA"));

        assertThrows(CastleException.class, () ->
                CryptoUtil.sign(TEST_DATA, keyPair256.getPrivate(), null));
    }

    @Test
    void testCrossKeyVerification() throws CastleException {
        // Sign with one key and try to verify with another
        byte[] signature = CryptoUtil.sign(TEST_DATA, keyPair256.getPrivate(), "SHA256withECDSA");
        assertFalse(CryptoUtil.verify(TEST_DATA,
                new String(Hex.encode(signature)),
                keyPair384.getPublic(),
                "SHA256withECDSA"));
    }
}