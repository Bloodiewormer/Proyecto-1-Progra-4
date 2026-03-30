-- =============================================================
--  BOLSA DE EMPLEO — Script completo de creación de base de datos
--  Compatible con: MySQL 8.x
--  Uso: mysql -u root -p < bolsa_empleo_setup.sql
-- =============================================================

-- Crear y seleccionar la base de datos
CREATE DATABASE IF NOT EXISTS javamvc
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE javamvc;

-- =============================================================
--  TABLAS
-- =============================================================

CREATE TABLE IF NOT EXISTS `usuario` (
    `id_usuario`     INT          PRIMARY KEY AUTO_INCREMENT,
    `correo`         VARCHAR(255) UNIQUE,
    `identificacion` VARCHAR(255) UNIQUE,
    `password_hash`  VARCHAR(255) NOT NULL,
    `rol`            VARCHAR(255) NOT NULL COMMENT 'ENUM: ADMIN | EMPRESA | OFERENTE',
    `estado`         VARCHAR(255) NOT NULL DEFAULT 'PENDIENTE'
                         COMMENT 'ENUM: PENDIENTE | ACTIVO | INACTIVO',
    `fecha_creacion` DATETIME     DEFAULT (CURRENT_TIMESTAMP),
    CONSTRAINT chk_usuario_rol CHECK (
        rol IN ('ADMIN', 'EMPRESA', 'OFERENTE')
    ),
    CONSTRAINT chk_usuario_credenciales CHECK (
        correo IS NOT NULL AND identificacion IS NOT NULL
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `empresa` (
    `id_empresa`  INT          PRIMARY KEY AUTO_INCREMENT,
    `id_usuario`  INT          UNIQUE NOT NULL,
    `nombre`      VARCHAR(255) NOT NULL,
    `localizacion`VARCHAR(255) NOT NULL,
    `telefono`    VARCHAR(255) NOT NULL,
    `descripcion` TEXT         NOT NULL,
    CONSTRAINT fk_empresa_usuario
        FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `oferente` (
    `id_oferente`       INT          PRIMARY KEY AUTO_INCREMENT,
    `id_usuario`        INT          UNIQUE NOT NULL,
    `num_identificacion`VARCHAR(255) UNIQUE NOT NULL
                            COMMENT 'Cedula personal del oferente',
    `nombre`            VARCHAR(255) NOT NULL,
    `apellido`          VARCHAR(255) NOT NULL,
    `nacionalidad`      VARCHAR(255) NOT NULL,
    `telefono`          VARCHAR(255) NOT NULL,
    `residencia`        VARCHAR(255) NOT NULL,
    `cv_path`           VARCHAR(255) COMMENT 'Ruta relativa al PDF subido. Ej: /uploads/cv/juan.pdf',
    CONSTRAINT fk_oferente_usuario
        FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `caracteristica` (
    `id_caracteristica` INT          PRIMARY KEY AUTO_INCREMENT,
    `nombre`            VARCHAR(255) NOT NULL,
    `id_padre`          INT          COMMENT 'NULL = raiz de jerarquia',
    CONSTRAINT fk_caracteristica_padre
        FOREIGN KEY (`id_padre`) REFERENCES `caracteristica` (`id_caracteristica`)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX IF NOT EXISTS idx_caracteristica_nombre_padre
    ON `caracteristica` (`nombre`, `id_padre`);


CREATE TABLE IF NOT EXISTS `puesto` (
    `id_puesto`        INT            PRIMARY KEY AUTO_INCREMENT,
    `id_empresa`       INT            NOT NULL,
    `titulo`           VARCHAR(255)   NOT NULL,
    `descripcion`      TEXT           NOT NULL,
    `salario`          DECIMAL(10, 2) NOT NULL COMMENT 'CK: salario > 0',
    `tipo_publicacion` VARCHAR(255)   NOT NULL COMMENT 'ENUM: PUBLICO | PRIVADO',
    `fecha_publicacion`TIMESTAMP      DEFAULT (CURRENT_TIMESTAMP),
    `estado`           VARCHAR(255)   NOT NULL DEFAULT 'ACTIVO'
                           COMMENT 'ENUM: ACTIVO | INACTIVO',
    CONSTRAINT chk_puesto_salario        CHECK (salario > 0),
    CONSTRAINT chk_puesto_tipo           CHECK (tipo_publicacion IN ('PUBLICO', 'PRIVADO')),
    CONSTRAINT chk_puesto_estado         CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT fk_puesto_empresa
        FOREIGN KEY (`id_empresa`) REFERENCES `empresa` (`id_empresa`)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `oferente_caracteristica` (
    `id_oferente`       INT NOT NULL,
    `id_caracteristica` INT NOT NULL,
    `nivel`             INT NOT NULL COMMENT 'CK: nivel BETWEEN 1 AND 5',
    PRIMARY KEY (`id_oferente`, `id_caracteristica`),
    CONSTRAINT chk_oferente_caracteristica_nivel CHECK (nivel BETWEEN 1 AND 5),
    CONSTRAINT fk_of_car_oferente
        FOREIGN KEY (`id_oferente`) REFERENCES `oferente` (`id_oferente`)
        ON DELETE CASCADE,
    CONSTRAINT fk_of_car_caracteristica
        FOREIGN KEY (`id_caracteristica`) REFERENCES `caracteristica` (`id_caracteristica`)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `puesto_caracteristica` (
    `id_puesto`         INT NOT NULL,
    `id_caracteristica` INT NOT NULL,
    `nivel_requerido`   INT NOT NULL COMMENT 'CK: nivel_requerido BETWEEN 1 AND 5',
    PRIMARY KEY (`id_puesto`, `id_caracteristica`),
    CONSTRAINT chk_puesto_caracteristica_nivel CHECK (nivel_requerido BETWEEN 1 AND 5),
    CONSTRAINT fk_pu_car_puesto
        FOREIGN KEY (`id_puesto`) REFERENCES `puesto` (`id_puesto`)
        ON DELETE CASCADE,
    CONSTRAINT fk_pu_car_caracteristica
        FOREIGN KEY (`id_caracteristica`) REFERENCES `caracteristica` (`id_caracteristica`)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================
--  DATOS SEMILLA — Usuarios de prueba
--  Contraseña para todos: Clave123
-- =============================================================

INSERT INTO usuario (correo, identificacion, password_hash, rol, estado)
VALUES
    ('admin.test@demo.com',    'ADMIN-DEMO-001',
     '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'ADMIN',    'ACTIVO'),
    ('empresa.test@demo.com',  'EMP-DEMO-001',
     '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'EMPRESA',  'ACTIVO'),
    ('oferente.test@demo.com', 'OFE-DEMO-001',
     '$2a$10$KqLBgvE/HUh.IL6d3rtCE.htwVMvCkyDZDnqItVUGvs0ibG0V6DAG', 'OFERENTE', 'ACTIVO');


INSERT INTO empresa (id_usuario, nombre, localizacion, telefono, descripcion)
SELECT id_usuario, 'Empresa Demo', 'San José, CR', '2222-3333',
       'Empresa de prueba para validar el flujo base del sistema.'
FROM usuario WHERE correo = 'empresa.test@demo.com';


INSERT INTO oferente (id_usuario, num_identificacion, nombre, apellido,
                      nacionalidad, telefono, residencia, cv_path)
SELECT id_usuario, 'OFE-DEMO-001', 'Ana', 'Prueba',
       'Costarricense', '8888-9999', 'Heredia, CR', '/uploads/cv/ana-prueba.pdf'
FROM usuario WHERE correo = 'oferente.test@demo.com';


-- =============================================================
--  CARACTERÍSTICAS (jerarquía: categoría padre → subcategorías)
-- =============================================================

INSERT INTO caracteristica (nombre, id_padre) VALUES
    ('Lenguajes de programación', NULL),
    ('Bases de Datos',            NULL),
    ('Tecnologías Web',           NULL),
    ('Testing',                   NULL),
    ('Ciberseguridad',            NULL);

-- Subcategorías — Lenguajes de programación
INSERT INTO caracteristica (nombre, id_padre)
SELECT nombre, (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Lenguajes de programación' AND id_padre IS NULL LIMIT 1)
FROM (
    SELECT 'Java'   AS nombre UNION ALL
    SELECT 'C#'              UNION ALL
    SELECT 'Python'          UNION ALL
    SELECT 'Kotlin'
) sub;

-- Subcategorías — Bases de Datos
INSERT INTO caracteristica (nombre, id_padre)
SELECT nombre, (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Bases de Datos' AND id_padre IS NULL LIMIT 1)
FROM (
    SELECT 'MySQL'      AS nombre UNION ALL
    SELECT 'Oracle'              UNION ALL
    SELECT 'PostgreSQL'
) sub;

-- Subcategorías — Tecnologías Web
INSERT INTO caracteristica (nombre, id_padre)
SELECT nombre, (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Tecnologías Web' AND id_padre IS NULL LIMIT 1)
FROM (
    SELECT 'HTML'       AS nombre UNION ALL
    SELECT 'CSS'                  UNION ALL
    SELECT 'JavaScript'
) sub;

-- Subcategorías — Testing
INSERT INTO caracteristica (nombre, id_padre)
SELECT nombre, (SELECT id_caracteristica FROM caracteristica WHERE nombre = 'Testing' AND id_padre IS NULL LIMIT 1)
FROM (
    SELECT 'JUnit'      AS nombre UNION ALL
    SELECT 'Assertions'          UNION ALL
    SELECT 'Selenium'
) sub;


-- =============================================================
--  PUESTOS Y SUS CARACTERÍSTICAS REQUERIDAS
-- =============================================================

-- Variables auxiliares (usamos SET para claridad)
SET @id_empresa = (SELECT id_empresa FROM empresa WHERE nombre = 'Empresa Demo' LIMIT 1);

SET @java   = (SELECT c.id_caracteristica FROM caracteristica c JOIN caracteristica p ON c.id_padre = p.id_caracteristica WHERE c.nombre = 'Java'       AND p.nombre = 'Lenguajes de programación' LIMIT 1);
SET @mysql  = (SELECT c.id_caracteristica FROM caracteristica c JOIN caracteristica p ON c.id_padre = p.id_caracteristica WHERE c.nombre = 'MySQL'      AND p.nombre = 'Bases de Datos'            LIMIT 1);
SET @html   = (SELECT c.id_caracteristica FROM caracteristica c JOIN caracteristica p ON c.id_padre = p.id_caracteristica WHERE c.nombre = 'HTML'       AND p.nombre = 'Tecnologías Web'           LIMIT 1);
SET @css    = (SELECT c.id_caracteristica FROM caracteristica c JOIN caracteristica p ON c.id_padre = p.id_caracteristica WHERE c.nombre = 'CSS'        AND p.nombre = 'Tecnologías Web'           LIMIT 1);
SET @junit  = (SELECT c.id_caracteristica FROM caracteristica c JOIN caracteristica p ON c.id_padre = p.id_caracteristica WHERE c.nombre = 'JUnit'      AND p.nombre = 'Testing'                   LIMIT 1);


-- Puesto 1: Full Stack Developer (PÚBLICO)
INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
VALUES (@id_empresa, 'Full Stack Developer',
        'Desarrollador full stack con experiencia en Java y tecnologías web modernas.',
        2000.00, 'PUBLICO', 'ACTIVO');

SET @p1 = LAST_INSERT_ID();

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido) VALUES
    (@p1, @java,  3),
    (@p1, @mysql, 3),
    (@p1, @html,  2);


-- Puesto 2: Backend Java Developer (PÚBLICO)
INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
VALUES (@id_empresa, 'Backend Java Developer',
        'Desarrollador backend especializado en Java y Spring Boot con conocimiento en BD relacionales.',
        1800.00, 'PUBLICO', 'ACTIVO');

SET @p2 = LAST_INSERT_ID();

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido) VALUES
    (@p2, @java,  4),
    (@p2, @mysql, 4),
    (@p2, @junit, 3);


-- Puesto 3: Frontend Developer (PRIVADO)
INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
VALUES (@id_empresa, 'Frontend Developer',
        'Desarrollador frontend con sólida experiencia en HTML, CSS y maquetación responsive.',
        1500.00, 'PRIVADO', 'ACTIVO');

SET @p3 = LAST_INSERT_ID();

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido) VALUES
    (@p3, @html, 4),
    (@p3, @css,  3);


-- Puesto 4: QA Testing Engineer (PÚBLICO)
INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
VALUES (@id_empresa, 'QA Testing Engineer',
        'Ingeniero de calidad con experiencia en automatización de pruebas con JUnit y Selenium.',
        1600.00, 'PUBLICO', 'ACTIVO');

SET @p4 = LAST_INSERT_ID();

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido) VALUES
    (@p4, @junit, 4);


-- Puesto 5: Database Administrator (PÚBLICO)
INSERT INTO puesto (id_empresa, titulo, descripcion, salario, tipo_publicacion, estado)
VALUES (@id_empresa, 'Database Administrator',
        'Administrador de bases de datos con experiencia en MySQL y diseño de esquemas.',
        1700.00, 'PUBLICO', 'ACTIVO');

SET @p5 = LAST_INSERT_ID();

INSERT INTO puesto_caracteristica (id_puesto, id_caracteristica, nivel_requerido) VALUES
    (@p5, @mysql, 5);


-- =============================================================
--  HABILIDADES DEL OFERENTE DEMO
-- =============================================================

SET @oferente_demo = (SELECT id_oferente FROM oferente WHERE num_identificacion = 'OFE-DEMO-001' LIMIT 1);

INSERT INTO oferente_caracteristica (id_oferente, id_caracteristica, nivel) VALUES
    (@oferente_demo, @java,  5),
    (@oferente_demo, @mysql, 4),
    (@oferente_demo, @html,  4),
    (@oferente_demo, @junit, 4),
    (@oferente_demo, @css,   3);


-- =============================================================
--  FIN DEL SCRIPT
-- =============================================================
SELECT 'Base de datos javamvc creada exitosamente.' AS resultado;
