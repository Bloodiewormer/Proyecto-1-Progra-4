ALTER TABLE `puesto` ADD CHECK (salario > 0);

ALTER TABLE `oferente_caracteristica` ADD CHECK (nivel BETWEEN 1 AND 5);

ALTER TABLE `puesto_caracteristica` ADD CHECK (nivel_requerido BETWEEN 1 AND 5);




