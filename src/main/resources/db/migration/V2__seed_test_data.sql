-- V2__seed_test_data.sql
-- Datos de prueba para login con Spring Security + BCryptPasswordEncoder.
--
-- Credenciales sugeridas:
--   1) EMPRESA  → correo: empresa.test@demo.com | identificacion: EMP-DEMO-001 | clave: Clave123
--   2) OFERENTE → correo: oferente.test@demo.com | identificacion: OFE-DEMO-001 | clave: Clave123
--   3) ADMIN    → correo: admin.test@demo.com   | identificacion: ADMIN-DEMO-001 | clave: Clave123
--
-- Los hashes BCrypt fueron generados con BCryptPasswordEncoder (cost factor 10).
-- NOTA: Estos hashes son válidos pero deben reemplazarse en producción.
-- Ver: src/test/java/GenerateBcryptHashes.java para generar hashes nuevos.
-- =============================================================

-- Empresa de prueba (estado: ACTIVO para poder loguear)
INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
SELECT 'empresa.test@demo.com', 'EMP-DEMO-001', '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'EMPRESA', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'empresa.test@demo.com' OR identificacion = 'EMP-DEMO-001'
);

-- Oferente de prueba (estado: ACTIVO para poder loguear)
INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
SELECT 'oferente.test@demo.com', 'OFE-DEMO-001', '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'OFERENTE', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'oferente.test@demo.com' OR identificacion = 'OFE-DEMO-001'
);

-- Admin de prueba (estado: ACTIVO por defecto para ADMIN)
INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
SELECT 'admin.test@demo.com', 'ADMIN-DEMO-001', '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'ADMIN', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'admin.test@demo.com' OR identificacion = 'ADMIN-DEMO-001'
);

-- Empresa vinculada al usuario empresa.test@demo.com
INSERT INTO empresa (id_usuario, nombre, localizacion, telefono, descripcion)
SELECT u.id_usuario, 'Empresa Demo', 'San Jose, CR', '2222-3333', 'Empresa de prueba para validar login y flujo base.'
FROM usuario u
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM empresa e WHERE e.id_usuario = u.id_usuario
  );

-- Oferente vinculado al usuario oferente.test@demo.com
INSERT INTO oferente (id_usuario, num_identificacion, nombre, apellido, nacionalidad, telefono, residencia, cv_path)
SELECT u.id_usuario, 'OFE-DEMO-001', 'Ana', 'Prueba', 'Costarricense', '8888-9999', 'Heredia, CR', '/uploads/cv/ana-prueba.pdf'
FROM usuario u
WHERE u.correo = 'oferente.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM oferente o WHERE o.id_usuario = u.id_usuario
  );

