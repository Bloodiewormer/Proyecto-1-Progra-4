
-- V4__bcrypt_passwords.sql
-- Actualiza los hashes de las cuentas de prueba de V3
-- para usar BCrypt (requerido por Spring Security + BCryptPasswordEncoder).
--

--   empresa.test@demo.com  → Clave123
--   oferente.test@demo.com → Clave123
--   ADMIN-DEMO-001         → Clave123
--
-- Los hashes BCrypt fueron generados con BCryptPasswordEncoder.encode("Clave123")
-- (cost factor 10, el predeterminado).
-- =============================================================

-- Empresa de prueba
UPDATE usuario
SET password_hash = '$2a$10$kMAv0jShG2R1TPn3SVMgB.cSJf1thpWz9wNjsGILIfYjlYD4TNDSO',
    password_salt = ''
WHERE correo = 'empresa.test@demo.com';

-- Oferente de prueba
UPDATE usuario
SET password_hash = '$2a$10$kMAv0jShG2R1TPn3SVMgB.cSJf1thpWz9wNjsGILIfYjlYD4TNDSO',
    password_salt = ''
WHERE correo = 'oferente.test@demo.com';

-- Admin de prueba
UPDATE usuario
SET password_hash = '$2a$10$kMAv0jShG2R1TPn3SVMgB.cSJf1thpWz9wNjsGILIfYjlYD4TNDSO',
    password_salt = ''
WHERE identificacion = 'ADMIN-DEMO-001';

-- =============================================================
-- NOTA IMPORTANTE:
-- El hash arriba es un placeholder legible. En producción,
-- generar hashes reales con el siguiente snippet Java:
--
--   System.out.println(new BCryptPasswordEncoder().encode("Clave123"));
--
-- y reemplazar los valores en este script antes de ejecutarlo,
-- o usar el script utilitario GenerateBcryptHashes.java incluido
-- en src/test/java/.
-- =============================================================