-- V2__seed_test_data.sql
-- Datos de prueba para login con Spring Security + BCryptPasswordEncoder.
-- Incluye caracteristicas jerarquicas, puestos demo y habilidades de oferente.
--
-- Credenciales sugeridas:
--   1) EMPRESA  -> correo: empresa.test@demo.com | identificacion: EMP-DEMO-001 | clave: Clave123
--   2) OFERENTE -> correo: oferente.test@demo.com | identificacion: OFE-DEMO-001 | clave: Clave123
--   3) ADMIN    -> correo: admin.test@demo.com   | identificacion: ADMIN-DEMO-001 | clave: Clave123
--
-- Los hashes BCrypt fueron generados con BCryptPasswordEncoder (cost factor 10).
-- NOTA: Estos hashes son validos pero deben reemplazarse en produccion.
-- =============================================================

-- 1) Usuarios base de prueba
INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
SELECT 'empresa.test@demo.com', 'EMP-DEMO-001', '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'EMPRESA', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'empresa.test@demo.com' OR identificacion = 'EMP-DEMO-001'
);

INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
SELECT 'oferente.test@demo.com', 'OFE-DEMO-001', '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'OFERENTE', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'oferente.test@demo.com' OR identificacion = 'OFE-DEMO-001'
);

INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
SELECT 'admin.test@demo.com', 'ADMIN-DEMO-001', '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'ADMIN', 'ACTIVO'
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE correo = 'admin.test@demo.com' OR identificacion = 'ADMIN-DEMO-001'
);

-- 2) Empresa y oferente demo
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

-- 3) Caracteristicas raiz
INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Lenguajes de programación', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM caracteristica WHERE nombre = 'Lenguajes de programación' AND id_padre IS NULL
);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Bases de Datos', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM caracteristica WHERE nombre = 'Bases de Datos' AND id_padre IS NULL
);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Tecnologías Web', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM caracteristica WHERE nombre = 'Tecnologías Web' AND id_padre IS NULL
);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Testing', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM caracteristica WHERE nombre = 'Testing' AND id_padre IS NULL
);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Ciberseguridad', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM caracteristica WHERE nombre = 'Ciberseguridad' AND id_padre IS NULL
);

SET @lng = (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Lenguajes de programación' AND id_padre IS NULL LIMIT 1);
SET @bd = (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Bases de Datos' AND id_padre IS NULL LIMIT 1);
SET @tw = (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Tecnologías Web' AND id_padre IS NULL LIMIT 1);
SET @testing = (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Testing' AND id_padre IS NULL LIMIT 1);

-- 4) Sub-caracteristicas
INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Java', @lng FROM DUAL
WHERE @lng IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'Java' AND id_padre = @lng);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'C#', @lng FROM DUAL
WHERE @lng IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'C#' AND id_padre = @lng);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Python', @lng FROM DUAL
WHERE @lng IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'Python' AND id_padre = @lng);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Kotlin', @lng FROM DUAL
WHERE @lng IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'Kotlin' AND id_padre = @lng);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'MySQL', @bd FROM DUAL
WHERE @bd IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'MySQL' AND id_padre = @bd);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Oracle', @bd FROM DUAL
WHERE @bd IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'Oracle' AND id_padre = @bd);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'PostgreSQL', @bd FROM DUAL
WHERE @bd IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'PostgreSQL' AND id_padre = @bd);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'HTML', @tw FROM DUAL
WHERE @tw IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'HTML' AND id_padre = @tw);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'CSS', @tw FROM DUAL
WHERE @tw IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'CSS' AND id_padre = @tw);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'JavaScript', @tw FROM DUAL
WHERE @tw IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'JavaScript' AND id_padre = @tw);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'JUnit', @testing FROM DUAL
WHERE @testing IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'JUnit' AND id_padre = @testing);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Assertions', @testing FROM DUAL
WHERE @testing IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'Assertions' AND id_padre = @testing);

INSERT INTO caracteristica (nombre, id_padre)
SELECT 'Selenium', @testing FROM DUAL
WHERE @testing IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM caracteristica WHERE nombre = 'Selenium' AND id_padre = @testing);

SET @java = (
    SELECT c.id_caracteristica
    FROM caracteristica c
    JOIN caracteristica p ON p.id_caracteristica = c.id_padre
    WHERE c.nombre = 'Java' AND p.nombre = 'Lenguajes de programación'
    LIMIT 1
);
SET @mysql = (
    SELECT c.id_caracteristica
    FROM caracteristica c
    JOIN caracteristica p ON p.id_caracteristica = c.id_padre
    WHERE c.nombre = 'MySQL' AND p.nombre = 'Bases de Datos'
    LIMIT 1
);
SET @html = (
    SELECT c.id_caracteristica
    FROM caracteristica c
    JOIN caracteristica p ON p.id_caracteristica = c.id_padre
    WHERE c.nombre = 'HTML' AND p.nombre = 'Tecnologías Web'
    LIMIT 1
);
SET @css = (
    SELECT c.id_caracteristica
    FROM caracteristica c
    JOIN caracteristica p ON p.id_caracteristica = c.id_padre
    WHERE c.nombre = 'CSS' AND p.nombre = 'Tecnologías Web'
    LIMIT 1
);
SET @junit = (
    SELECT c.id_caracteristica
    FROM caracteristica c
    JOIN caracteristica p ON p.id_caracteristica = c.id_padre
    WHERE c.nombre = 'JUnit' AND p.nombre = 'Testing'
    LIMIT 1
);

-- 5) Puestos de prueba (empresa demo)
INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
SELECT e.id_empresa,
       'Full Stack Developer',
       'Desarrollador full stack con experiencia en Java y tecnologías web modernas.',
       2000.00, 'PUBLICO', 'ACTIVO'
FROM empresa e
JOIN usuario u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM puesto p WHERE p.id_empresa = e.id_empresa AND p.titulo = 'Full Stack Developer'
  );

SET @p1 = (
    SELECT p.id_puesto
    FROM puesto p
    JOIN empresa e ON p.id_empresa = e.id_empresa
    JOIN usuario u ON e.id_usuario = u.id_usuario
    WHERE u.correo = 'empresa.test@demo.com'
      AND p.titulo = 'Full Stack Developer'
    LIMIT 1
);

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p1, @java, 3 FROM DUAL
WHERE @p1 IS NOT NULL AND @java IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p1 AND id_caracteristica = @java
  );

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p1, @mysql, 3 FROM DUAL
WHERE @p1 IS NOT NULL AND @mysql IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p1 AND id_caracteristica = @mysql
  );

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p1, @html, 2 FROM DUAL
WHERE @p1 IS NOT NULL AND @html IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p1 AND id_caracteristica = @html
  );

INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
SELECT e.id_empresa,
       'Backend Java Developer',
       'Desarrollador backend especializado en Java y Spring Boot con conocimiento en BD relacionales.',
       1800.00, 'PUBLICO', 'ACTIVO'
FROM empresa e
JOIN usuario u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM puesto p WHERE p.id_empresa = e.id_empresa AND p.titulo = 'Backend Java Developer'
  );

SET @p2 = (
    SELECT p.id_puesto
    FROM puesto p
    JOIN empresa e ON p.id_empresa = e.id_empresa
    JOIN usuario u ON e.id_usuario = u.id_usuario
    WHERE u.correo = 'empresa.test@demo.com'
      AND p.titulo = 'Backend Java Developer'
    LIMIT 1
);

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p2, @java, 4 FROM DUAL
WHERE @p2 IS NOT NULL AND @java IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p2 AND id_caracteristica = @java
  );

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p2, @mysql, 4 FROM DUAL
WHERE @p2 IS NOT NULL AND @mysql IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p2 AND id_caracteristica = @mysql
  );

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p2, @junit, 3 FROM DUAL
WHERE @p2 IS NOT NULL AND @junit IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p2 AND id_caracteristica = @junit
  );

INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
SELECT e.id_empresa,
       'Frontend Developer',
       'Desarrollador frontend con sólida experiencia en HTML, CSS y maquetación responsive.',
       1500.00, 'PRIVADO', 'ACTIVO'
FROM empresa e
JOIN usuario u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM puesto p WHERE p.id_empresa = e.id_empresa AND p.titulo = 'Frontend Developer'
  );

SET @p3 = (
    SELECT p.id_puesto
    FROM puesto p
    JOIN empresa e ON p.id_empresa = e.id_empresa
    JOIN usuario u ON e.id_usuario = u.id_usuario
    WHERE u.correo = 'empresa.test@demo.com'
      AND p.titulo = 'Frontend Developer'
    LIMIT 1
);

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p3, @html, 4 FROM DUAL
WHERE @p3 IS NOT NULL AND @html IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p3 AND id_caracteristica = @html
  );

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p3, @css, 3 FROM DUAL
WHERE @p3 IS NOT NULL AND @css IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p3 AND id_caracteristica = @css
  );

INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
SELECT e.id_empresa,
       'QA Testing Engineer',
       'Ingeniero de calidad con experiencia en automatización de pruebas con JUnit y Selenium.',
       1600.00, 'PUBLICO', 'ACTIVO'
FROM empresa e
JOIN usuario u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM puesto p WHERE p.id_empresa = e.id_empresa AND p.titulo = 'QA Testing Engineer'
  );

SET @p4 = (
    SELECT p.id_puesto
    FROM puesto p
    JOIN empresa e ON p.id_empresa = e.id_empresa
    JOIN usuario u ON e.id_usuario = u.id_usuario
    WHERE u.correo = 'empresa.test@demo.com'
      AND p.titulo = 'QA Testing Engineer'
    LIMIT 1
);

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p4, @junit, 4 FROM DUAL
WHERE @p4 IS NOT NULL AND @junit IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p4 AND id_caracteristica = @junit
  );

INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
SELECT e.id_empresa,
       'Database Administrator',
       'Administrador de bases de datos con experiencia en MySQL y diseño de esquemas.',
       1700.00, 'PUBLICO', 'ACTIVO'
FROM empresa e
JOIN usuario u ON e.id_usuario = u.id_usuario
WHERE u.correo = 'empresa.test@demo.com'
  AND NOT EXISTS (
      SELECT 1 FROM puesto p WHERE p.id_empresa = e.id_empresa AND p.titulo = 'Database Administrator'
  );

SET @p5 = (
    SELECT p.id_puesto
    FROM puesto p
    JOIN empresa e ON p.id_empresa = e.id_empresa
    JOIN usuario u ON e.id_usuario = u.id_usuario
    WHERE u.correo = 'empresa.test@demo.com'
      AND p.titulo = 'Database Administrator'
    LIMIT 1
);

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido)
SELECT @p5, @mysql, 5 FROM DUAL
WHERE @p5 IS NOT NULL AND @mysql IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM puesto_caracteristica WHERE id_puesto = @p5 AND id_caracteristica = @mysql
  );

-- 6) Habilidades del oferente demo (Ana Prueba)
SET @oferente_demo = (
    SELECT o.id_oferente
    FROM oferente o
    JOIN usuario u ON o.id_usuario = u.id_usuario
    WHERE u.correo = 'oferente.test@demo.com'
    LIMIT 1
);

INSERT INTO oferente_caracteristica (id_oferente, id_caracteristica, nivel)
SELECT @oferente_demo, @java, 5 FROM DUAL
WHERE @oferente_demo IS NOT NULL AND @java IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM oferente_caracteristica WHERE id_oferente = @oferente_demo AND id_caracteristica = @java
  );

INSERT INTO oferente_caracteristica (id_oferente, id_caracteristica, nivel)
SELECT @oferente_demo, @mysql, 4 FROM DUAL
WHERE @oferente_demo IS NOT NULL AND @mysql IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM oferente_caracteristica WHERE id_oferente = @oferente_demo AND id_caracteristica = @mysql
  );

INSERT INTO oferente_caracteristica (id_oferente, id_caracteristica, nivel)
SELECT @oferente_demo, @html, 4 FROM DUAL
WHERE @oferente_demo IS NOT NULL AND @html IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM oferente_caracteristica WHERE id_oferente = @oferente_demo AND id_caracteristica = @html
  );

INSERT INTO oferente_caracteristica (id_oferente, id_caracteristica, nivel)
SELECT @oferente_demo, @junit, 4 FROM DUAL
WHERE @oferente_demo IS NOT NULL AND @junit IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM oferente_caracteristica WHERE id_oferente = @oferente_demo AND id_caracteristica = @junit
  );

INSERT INTO oferente_caracteristica (id_oferente, id_caracteristica, nivel)
SELECT @oferente_demo, @css, 3 FROM DUAL
WHERE @oferente_demo IS NOT NULL AND @css IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM oferente_caracteristica WHERE id_oferente = @oferente_demo AND id_caracteristica = @css
  );

