# Proyecto #1 — Bolsa de Empleo (EIF401 Programación IV)

**Universidad Nacional — Facultad de Ciencias Exactas y Naturales — Escuela de Informática**  
**Curso: EIF401 Programación IV (2026-I)**

Este proyecto es una aplicación web de "Bolsa de Empleo" desarrollada con Spring Boot. Permite a las empresas publicar ofertas de trabajo y a los solicitantes (oferentes) buscarlas y aplicar a ellas.

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
- [Usuarios de Prueba](#usuarios-de-prueba)
- [Funcionalidades Principales](#funcionalidades-principales)
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
- **Generación de Documentos**: OpenPDF
- **Gestión de Dependencias**: Maven

---

## Requisitos
- Java JDK 17 o superior
- Apache Maven 3.6+
- MySQL 8.0 o superior
- Un IDE para Java (ej. IntelliJ IDEA, Eclipse, VS Code)

---

## Configuración de la Base de Datos
1.  **Verificar Script**: El script `bolsa_empleo_setup.sql` crea la base de datos `javamvc` y la puebla con tablas y datos de prueba.
2.  **Ejecutar Script**: Antes de iniciar la aplicación, ejecuta el script en tu instancia de MySQL. Esto asegurará que la estructura y los datos de prueba existan.
    ```bash
    # Desde la línea de comandos
    mysql -u root -p < bolsa_empleo_setup.sql
    ```
3.  **Configurar `application.properties`**: Asegúrate de que el fichero `src/main/resources/application.properties` apunte a la base de datos correcta (`javamvc`) y use las credenciales adecuadas para tu entorno local.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/javamvc
    spring.datasource.username=root
    spring.datasource.password=your_mysql_password
    ```

---

## Cómo Ejecutar
1.  Clona el repositorio.
2.  Asegúrate de haber ejecutado el script `bolsa_empleo_setup.sql` en tu MySQL.
3.  Verifica que la configuración en `application.properties` sea correcta.
4.  Ejecuta la aplicación usando el plugin de Maven para Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
5.  La aplicación estará disponible en `http://localhost:8080`.

---

## Usuarios de Prueba
El script `bolsa_empleo_setup.sql` crea los siguientes usuarios. La contraseña para todos es **`Clave123`**.

| Rol         | Correo (Usuario)         | Contraseña |
|-------------|--------------------------|------------|
| Administrador | `admin.test@demo.com`    | `123` |
| Empresa     | `empresa.test@demo.com`  | `123` |
| Oferente    | `oferente.test@demo.com` | `123` |

**Nota Importante sobre el CV**: El script de base de datos asigna una ruta de CV (`/uploads/cv/ana-prueba.pdf`) al usuario `oferente.test@demo.com`. Sin embargo, la aplicación espera que el archivo físico exista en el sistema de ficheros del servidor. Para que la funcionalidad de descarga de CV funcione correctamente, debes iniciar sesión como el oferente y **subir un archivo PDF**, que será guardado por la aplicación en la ubicación correcta.

---

## Funcionalidades Principales
- **Gestión de Usuarios**: Registro y autenticación con roles diferenciados (ADMIN, EMPRESA, OFERENTE).
- **Seguridad**: Rutas protegidas y gestión de sesiones con Spring Security.
- **CRUD de Ofertas de Trabajo**: Las empresas pueden crear, editar, y eliminar sus ofertas de empleo.
- **Subida de CV**: Los oferentes pueden subir su currículum en formato PDF.
- **Visualización de CV**: Las empresas pueden ver los CV de los oferentes que aplican a sus ofertas.
- **Aplicar a Ofertas**: Los oferentes pueden buscar y aplicar a las ofertas de trabajo disponibles.
- **Jerarquía de Habilidades**: El sistema maneja una jerarquía de características y habilidades.

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
