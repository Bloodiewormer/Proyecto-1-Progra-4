# Proyecto #1 - Bolsa de Empleo (EIF401 Programación IV)

**Universidad Nacional — Facultad de Ciencias Exactas y Naturales — Escuela de Informática**  
**Curso: EIF401 Programación IV (2026-I)**

Este proyecto es una aplicación web de "Bolsa de Empleo" desarrollada con Spring Boot. Permite a las empresas publicar ofertas de trabajo y a los solicitantes buscarlas y aplicar a ellas.

---
## Integrantes
- David Gonzalez Cordoba -- 208540087
- Christopher Rojas Montero -- 118960140
- Liseth Vallejos Gonzales -- 208390122

---

## Tabla de Contenidos
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Requisitos](#requisitos)
- [Configuración de la Base de Datos](#configuración-de-la-base-de-datos)
- [Cómo Ejecutar](#cómo-ejecutar)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Licencia](#licencia)

---

## Tecnologías Utilizadas
- **Framework**: Spring Boot
- **Lenguaje**: Java 17
- **Base de Datos**: MySQL
- **Motor de Plantillas**: Thymeleaf
- **Persistencia de Datos**: Spring Data JPA
- **Seguridad**: Spring Security
- **Migraciones de BD**: Flyway
- **Gestión de Dependencias**: Maven

---

## Requisitos
- Java JDK 17 o superior
- Apache Maven 3.6+
- MySQL 8.0 o superior
- Un IDE para Java (ej. IntelliJ IDEA, Eclipse, VS Code)

---

## Configuración de la Base de Datos
1.  Asegúrate de tener un servidor MySQL en ejecución.
2.  Crea una base de datos. El `pom.xml` está configurado para usar una base de datos llamada `javamvc`, pero puedes cambiarlo en `src/main/resources/application.properties`.
3.  El script de inicialización `bolsa_empleo_setup.sql` contiene el esquema y los datos iniciales. Puedes importarlo a tu base de datos usando una herramienta como MySQL Workbench o desde la línea de comandos:
    ```bash
    mysql -u tu_usuario -p tu_base_de_datos < bolsa_empleo_setup.sql
    ```
4.  Configura las credenciales de la base de datos en `src/main/resources/application.properties`. Ejemplo:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/bolsa_empleo_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    ```
    *Nota: El proyecto también utiliza Flyway para gestionar las migraciones de la base de datos. Las credenciales en el `pom.xml` para Flyway (`<user>root</user>`, `<password>1234</password>`) deberían ser actualizadas o, preferiblemente, gestionadas a través del archivo `application.properties` para mayor seguridad.*

---

## Cómo Ejecutar
1.  Clona el repositorio:
    ```bash
    git clone https://github.com/Bloodiewormer/Proyecto-1-Progra-4.git
    cd Proyecto-1-Progra-4
    ```
2.  Asegúrate de que la configuración de la base de datos en `application.properties` sea correcta.
3.  Ejecuta la aplicación usando el plugin de Maven para Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
4.  La aplicación estará disponible en `http://localhost:8080`.

---

## Estructura del Proyecto
```
Proyecto-1-Progra-4/
├── .mvn/                   # Wrapper de Maven
├── src/
│   ├── main/
│   │   ├── java/           # Código fuente de la aplicación Spring Boot
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/ # Scripts de migración de Flyway
│   │       ├── static/     # Archivos estáticos (CSS, JS, imágenes)
│   │       ├── templates/  # Plantillas de Thymeleaf
│   │       └── application.properties # Fichero de configuración de Spring
│   └── test/
│       └── java/           # Pruebas unitarias y de integración
├── bolsa_empleo_setup.sql  # Script de configuración inicial de la BD
├── pom.xml                 # Fichero de configuración de Maven
└── README.md
```

---

## Licencia
Este proyecto está bajo la Licencia MIT.
