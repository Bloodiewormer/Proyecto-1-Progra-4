package org.bolsa.empleo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar hashes BCrypt reales para el script V4__bcrypt_passwords.sql.
 *
 * CÓMO USAR:
 *  1. Ejecutar esta clase como programa Java normal (main).
 *  2. Copiar los hashes generados en la consola.
 *  3. Reemplazar los placeholders en V4__bcrypt_passwords.sql.
 *
 * Cada ejecución genera hashes diferentes (BCrypt usa salt aleatorio),
 * pero todos son válidos para la misma contraseña.
 */
public class GenerateBcryptHashes {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] passwords = {"Clave123"};  // agregar más si se necesitan

        for (String pwd : passwords) {
            System.out.println("Password: " + pwd);
            System.out.println("Hash:     " + encoder.encode(pwd));
            System.out.println("---");
        }
    }
}