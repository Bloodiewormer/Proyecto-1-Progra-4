package org.bolsa.empleo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBcryptHashes {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] passwords = {"Clave123"};

        for (String pwd : passwords) {
            System.out.println("Password: " + pwd);
            System.out.println("Hash:     " + encoder.encode(pwd));
            System.out.println("---");
        }
    }
}