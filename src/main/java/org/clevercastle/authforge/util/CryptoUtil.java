package org.clevercastle.authforge.util;

import org.clevercastle.authforge.CastleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

/**
 * @author XueJuncheng
 * @date 2023/6/2
 */
public class CryptoUtil {
    private static final Logger logger = LoggerFactory.getLogger(CryptoUtil.class);

    public static byte[] sign(String data, PrivateKey key, String algorithm) throws CastleException {
        if (data == null) {
            return null;
        }
        if (key == null || algorithm == null) {
            throw new CastleException("");
        }
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(key);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            signature.update(dataBytes);
            return signature.sign();
        } catch (Exception e) {
            logger.error("Fail to sign the data", e);
            throw new CastleException("");
        }
    }

    public static boolean verify(String data, String hexSignature, PublicKey publicKey, String algorithm) throws CastleException {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            signature.update(dataBytes);
            return signature.verify(Hex.decode(hexSignature));
        } catch (SignatureException e) {
            logger.error("Fail to verify the signature", e);
            return false;
        } catch (Exception e) {
            logger.error("Fail to verify the signature", e);
            throw new CastleException();
        }
    }

    public static byte[] derToJose(byte[] derSignature, int outputLength) {
        // Check the DER signature starts with the SEQUENCE tag (0x30)
        if (derSignature[0] != 0x30) {
            throw new IllegalArgumentException("Invalid DER signature");
        }

        // Get the total length of the sequence
        int totalLength = derSignature[1] & 0xFF;
        if (totalLength + 2 != derSignature.length) {
            throw new IllegalArgumentException("Invalid DER signature length");
        }

        int offset = 2;  // Skip the SEQUENCE tag and length field

        // Parse the r value
        if (derSignature[offset] != 0x02) {
            throw new IllegalArgumentException("Invalid INTEGER tag for r");
        }
        int rLength = derSignature[offset + 1] & 0xFF;
        byte[] r = new byte[rLength];
        System.arraycopy(derSignature, offset + 2, r, 0, rLength);
        offset += 2 + rLength;

        // Parse the s value
        if (derSignature[offset] != 0x02) {
            throw new IllegalArgumentException("Invalid INTEGER tag for s");
        }
        int sLength = derSignature[offset + 1] & 0xFF;
        byte[] s = new byte[sLength];
        System.arraycopy(derSignature, offset + 2, s, 0, sLength);

        // Remove leading zeros from r and s while ensuring at least one byte remains
        r = trimLeadingZeros(r);
        s = trimLeadingZeros(s);

        // Create the JOSE signature with proper padding
        byte[] joseSignature = new byte[outputLength * 2];

        // Copy r and s into the JOSE signature with proper padding
        int rOffset = outputLength - r.length;
        int sOffset = outputLength + (outputLength - s.length);
        if (rOffset < 0 || sOffset < outputLength) {
            throw new IllegalArgumentException("Invalid signature value: too large for output length");
        }

        System.arraycopy(r, 0, joseSignature, rOffset, r.length);
        System.arraycopy(s, 0, joseSignature, sOffset, s.length);

        return joseSignature;
    }

    private static byte[] trimLeadingZeros(byte[] input) {
        int offset = 0;
        while (offset < input.length - 1 && input[offset] == 0) {
            offset++;
        }
        byte[] result = new byte[input.length - offset];
        System.arraycopy(input, offset, result, 0, result.length);
        return result;
    }

    /**
     * Converts a JOSE signature format (r‖s) to an ASN.1 DER encoded ECDSA signature.
     *
     * @param joseSignature The JOSE formatted signature (concatenation of r and s).
     * @param outputLength  The fixed length of each of r and s (e.g., 32 bytes for P-256).
     * @return A DER-encoded signature byte array.
     * @throws Exception if an encoding error occurs.
     */
    public static byte[] joseToDer(byte[] joseSignature, int outputLength) throws Exception {
        if (joseSignature.length != outputLength * 2) {
            throw new IllegalArgumentException("Invalid JOSE signature length");
        }
        // Split the JOSE signature into r and s parts.
        byte[] r = Arrays.copyOfRange(joseSignature, 0, outputLength);
        byte[] s = Arrays.copyOfRange(joseSignature, outputLength, 2 * outputLength);
        // Remove leading zeros if present.
        byte[] rDer = trimLeadingZeros(r);
        byte[] sDer = trimLeadingZeros(s);
        // If the most significant bit is set, prepend a zero byte to indicate a positive INTEGER.
        if ((rDer[0] & 0x80) != 0) {
            rDer = prependZero(rDer);
        }
        if ((sDer[0] & 0x80) != 0) {
            sDer = prependZero(sDer);
        }
        // DER encode the signature as a SEQUENCE of two INTEGER values.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Write the DER SEQUENCE tag.
        baos.write(0x30);
        // Compute total length: 2 bytes for each INTEGER header (tag + length) plus the lengths of rDer and sDer.
        int totalLength = 2 + rDer.length + 2 + sDer.length;
        baos.write(totalLength);
        // Write r as a DER INTEGER.
        baos.write(0x02);         // INTEGER tag for r
        baos.write(rDer.length);  // length of r
        baos.write(rDer);         // r value
        // Write s as a DER INTEGER.
        baos.write(0x02);         // INTEGER tag for s
        baos.write(sDer.length);  // length of s
        baos.write(sDer);         // s value
        return baos.toByteArray();
    }

    /**
     * Prepends a 0x00 byte to the given byte array.
     *
     * @param bytes The byte array to prepend.
     * @return A new byte array with a 0x00 as the first byte.
     */
    private static byte[] prependZero(byte[] bytes) {
        byte[] output = new byte[bytes.length + 1];
        output[0] = 0x00;
        System.arraycopy(bytes, 0, output, 1, bytes.length);
        return output;
    }
}
