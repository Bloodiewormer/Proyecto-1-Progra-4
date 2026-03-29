CREATE TABLE `usuario` (
                           `id_usuario` int PRIMARY KEY AUTO_INCREMENT,
                           `correo` varchar(255) UNIQUE,
                           `identificacion` varchar(255) UNIQUE,
                           `password_hash` varchar(255) NOT NULL,
                           `rol` varchar(255) NOT NULL COMMENT 'ENUM: ADMIN | EMPRESA | OFERENTE',
                           `estado` varchar(255) NOT NULL DEFAULT 'PENDIENTE' COMMENT 'ENUM: PENDIENTE | ACTIVO | INACTIVO — ADMIN se inserta como ACTIVO',
                           `fecha_creacion` datetime DEFAULT (CURRENT_TIMESTAMP),
                           CONSTRAINT chk_usuario_credenciales CHECK (
                               rol IN ('ADMIN', 'EMPRESA', 'OFERENTE')
                               AND correo IS NOT NULL
                               AND identificacion IS NOT NULL
                           )
);

CREATE TABLE `empresa` (
                           `id_empresa` int PRIMARY KEY AUTO_INCREMENT,
                           `id_usuario` int UNIQUE NOT NULL,
                           `nombre` varchar(255) NOT NULL,
                           `localizacion` varchar(255) NOT NULL,
                           `telefono` varchar(255) NOT NULL,
                           `descripcion` text NOT NULL
);

CREATE TABLE `oferente` (
                            `id_oferente` int PRIMARY KEY AUTO_INCREMENT,
                            `id_usuario` int UNIQUE NOT NULL,
                            `num_identificacion` varchar(255) UNIQUE NOT NULL COMMENT 'Cedula personal del oferente',
                            `nombre` varchar(255) NOT NULL,
                            `apellido` varchar(255) NOT NULL,
                            `nacionalidad` varchar(255) NOT NULL,
                            `telefono` varchar(255) NOT NULL,
                            `residencia` varchar(255) NOT NULL,
                            `cv_path` varchar(255) COMMENT 'Ruta relativa al PDF subido. Ej: /uploads/cv/juan.pdf'
);

CREATE TABLE `puesto` (
                          `id_puesto` int PRIMARY KEY AUTO_INCREMENT,
                          `id_empresa` int NOT NULL,
                          `titulo` varchar(255) NOT NULL,
                          `descripcion` text NOT NULL,
                          `salario` decimal NOT NULL COMMENT 'CK: salario > 0',
                          `tipo_publicacion` varchar(255) NOT NULL COMMENT 'ENUM: PUBLICO | PRIVADO',
                          `fecha_publicacion` timestamp DEFAULT (CURRENT_TIMESTAMP),
                          `estado` varchar(255) NOT NULL DEFAULT 'ACTIVO' COMMENT 'ENUM: ACTIVO | INACTIVO',
                          CONSTRAINT chk_puesto_salario CHECK (salario > 0)
);

CREATE TABLE `caracteristica` (
                                  `id_caracteristica` int PRIMARY KEY AUTO_INCREMENT,
                                  `nombre` varchar(255) NOT NULL,
                                  `id_padre` int COMMENT 'NULL = raiz de jerarquia. CK: id_padre != id_caracteristica'
);

CREATE TABLE `oferente_caracteristica` (
                                           `id_oferente` int NOT NULL,
                                           `id_caracteristica` int NOT NULL,
                                           `nivel` int NOT NULL COMMENT 'CK: nivel BETWEEN 1 AND 5',
                                           PRIMARY KEY (`id_oferente`, `id_caracteristica`),
                                           CONSTRAINT chk_oferente_caracteristica_nivel CHECK (nivel BETWEEN 1 AND 5)
);

CREATE TABLE `puesto_caracteristica` (
                                         `id_puesto` int NOT NULL,
                                         `id_caracteristica` int NOT NULL,
                                         `nivel_requerido` int NOT NULL COMMENT 'CK: nivel_requerido BETWEEN 1 AND 5',
                                         PRIMARY KEY (`id_puesto`, `id_caracteristica`),
                                         CONSTRAINT chk_puesto_caracteristica_nivel CHECK (nivel_requerido BETWEEN 1 AND 5)
);


ALTER TABLE `empresa` ADD FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE RESTRICT;

ALTER TABLE `oferente` ADD FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE RESTRICT;

ALTER TABLE `puesto` ADD FOREIGN KEY (`id_empresa`) REFERENCES `empresa` (`id_empresa`) ON DELETE RESTRICT;

ALTER TABLE `caracteristica` ADD FOREIGN KEY (`id_padre`) REFERENCES `caracteristica` (`id_caracteristica`) ON DELETE RESTRICT;

ALTER TABLE `oferente_caracteristica` ADD FOREIGN KEY (`id_oferente`) REFERENCES `oferente` (`id_oferente`) ON DELETE CASCADE;

ALTER TABLE `oferente_caracteristica` ADD FOREIGN KEY (`id_caracteristica`) REFERENCES `caracteristica` (`id_caracteristica`) ON DELETE RESTRICT;

ALTER TABLE `puesto_caracteristica` ADD FOREIGN KEY (`id_puesto`) REFERENCES `puesto` (`id_puesto`) ON DELETE CASCADE;

ALTER TABLE `puesto_caracteristica` ADD FOREIGN KEY (`id_caracteristica`) REFERENCES `caracteristica` (`id_caracteristica`) ON DELETE RESTRICT;
