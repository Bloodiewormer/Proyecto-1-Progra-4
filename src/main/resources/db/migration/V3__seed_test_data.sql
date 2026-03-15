-- Datos de prueba para login (Flyway V3)
-- Credenciales sugeridas:
-- 1) EMPRESA  -> credencial: empresa.test@demo.com | clave: Clave123
-- 2) OFERENTE -> credencial: oferente.test@demo.com | clave: Clave123
-- 3) ADMIN    -> credencial: ADMIN-DEMO-001        | clave: Clave123

INSERT INTO usuario (correo, identificacion, password_hash, password_salt, rol, estado)
SELECT 'empresa.test@demo.com', NULL, SHA2(CONCAT('SALT_EMP_2026', 'Clave123'), 256), 'SALT_EMP_2026', 'EMPRESA', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'empresa.test@demo.com'
);

INSERT INTO usuario (correo, identificacion, password_hash, password_salt, rol, estado)
SELECT 'oferente.test@demo.com', NULL, SHA2(CONCAT('SALT_OFE_2026', 'Clave123'), 256), 'SALT_OFE_2026', 'OFERENTE', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'oferente.test@demo.com'
);

INSERT INTO usuario (correo, identificacion, password_hash, password_salt, rol, estado)
SELECT NULL, 'ADMIN-DEMO-001', SHA2(CONCAT('SALT_ADM_2026', 'Clave123'), 256), 'SALT_ADM_2026', 'ADMIN', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE identificacion = 'ADMIN-DEMO-001'
);

INSERT INTO empresa (id_usuario, nombre, localizacion, telefono, descripcion)
SELECT u.id_usuario, 'Empresa Demo', 'San Jose, CR', '2222-3333', 'Empresa de prueba para validar login y flujo base.'
FROM usuario u
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM empresa e WHERE e.id_usuario = u.id_usuario
  );

INSERT INTO oferente (id_usuario, num_identificacion, nombre, apellido, nacionalidad, telefono, residencia, cv_path)
SELECT u.id_usuario, 'OFE-DEMO-001', 'Ana', 'Prueba', 'Costarricense', '8888-9999', 'Heredia, CR', '/uploads/cv/ana-prueba.pdf'
FROM usuario u
WHERE u.correo = 'oferente.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM oferente o WHERE o.id_usuario = u.id_usuario
  );

