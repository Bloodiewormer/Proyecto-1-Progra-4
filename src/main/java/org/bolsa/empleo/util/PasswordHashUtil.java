package org.bolsa.empleo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHashUtil {
    private PasswordHashUtil() {
    }

    public static String hash(String clave, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest((salt + clave).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashed.length * 2);
            for (byte current : hashed) {
                sb.append(String.format("%02x", current));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No se pudo inicializar SHA-256", ex);
        }
    }
}

