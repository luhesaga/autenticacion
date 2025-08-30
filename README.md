# Microservicio de Autenticación - CrediYa

Este microservicio es el encargado de gestionar todos los procesos de autenticación y administración de usuarios para la plataforma **CrediYa**. Implementado como parte de un sistema de microservicios, sigue los principios de la **Arquitectura Limpia (Hexagonal)** para asegurar un bajo acoplamiento, alta cohesión y facilidad de mantenimiento.

## Historia de Usuario Implementada (HU1): Registro de Usuarios

La funcionalidad principal desarrollada hasta ahora es el **registro de nuevos usuarios en el sistema**.

**Funcionalidades Clave:**
* Exposición de un endpoint `POST` para la creación de usuarios.
* Recepción de datos personales y de salario del nuevo usuario.
* Validación de la información de entrada, incluyendo:
    * Campos obligatorios (nombre, apellido, email).
    * Formato de datos (email, rangos numéricos).
    * Reglas de negocio (unicidad de email y documento de identidad).
* Persistencia del nuevo usuario en la base de datos.
* Manejo de errores controlado y consistente para el cliente de la API.

---

## ?? Arquitectura y Tecnologías

* **Arquitectura**: Limpia / Hexagonal, siguiendo el scaffold de Bancolombia.
* **Lenguaje**: Java 17
* **Framework**: Spring Boot 3 con WebFlux (Programación Reactiva)
* **Base de Datos**: PostgreSQL
* **Capa de Persistencia**: R2DBC (Reactiva)
* **Gestor de Dependencias**: Gradle

---

## ? Estructura del Proyecto

El proyecto está organizado en módulos, separando claramente las responsabilidades:

* `applications/app-service`: Módulo principal que ensambla y ejecuta la aplicación.
* `domain/model`: Contiene las entidades de negocio (`Usuario.java`) y los contratos o puertos (`UsuarioRepository.java`). Es el corazón del software.
* `domain/usecase`: Orquesta la lógica de negocio (`UsuarioUseCase.java`).
* `infrastructure/entry-points/api-rest`: Implementa la capa de entrada, en este caso, el controlador REST (`ApiRest.java`) que expone la API.
* `infrastructure/driven-adapters/r2dbc-postgresql`: Implementa la comunicación con la base de datos (el "adaptador de salida").

---

## ? Cómo Ejecutar el Proyecto

### Pre-requisitos
* JDK 17 o superior.
* Gradle 7.x o superior.
* Tener una instancia de PostgreSQL corriendo (se recomienda usar Docker).

### Configuración
1.  Clona el repositorio.
2.  Navega al archivo `applications/app-service/src/main/resources/application.yml`.
3.  Asegúrate de que las propiedades de la base de datos coincidan con tu configuración local. El prefijo usado es `adapters.r2dbc`:
    ```yaml
    adapters:
      r2dbc:
        host: localhost
        port: 5432
        database: crediyadb
        schema: public
        username: tu_usuario
        password: tu_contraseña
    ```

### Ejecución
Abre una terminal en la raíz del proyecto y ejecuta el siguiente comando Gradle:

```bash
./gradlew bootRun
```
El servicio estará disponible en `http://localhost:8080`.

---

## ? Endpoints de la API (HU1)

### Registrar un Nuevo Usuario
* **Método:** `POST`
* **URL:** `/api/v1/usuarios`
* **Descripción:** Crea un nuevo usuario en el sistema.

**Ejemplo de Request Body:**
```json
{
  "nombre": "Ana",
  "apellido": "García",
  "email": "ana.garcia@test.com",
  "documentoIdentidad": "12345678",
  "telefono": "3001234567",
  "direccion": "Calle Falsa 123",
  "fechaNacimiento": "1990-05-15",
  "idRol": 1,
  "salarioBase": 5000000
}
```

**Respuesta Exitosa (201 CREATED):**
```json
{
    "idUsuario": 1,
    "nombre": "Ana",
    "apellido": "García",
    "email": "ana.garcia@test.com",
    // ... resto de campos
}
```

**Respuesta de Error (400 BAD REQUEST):**
```json
{
    "message": "El correo electronico ya esta registrado."
}
```

### Documentación Interactiva (Swagger)
Una vez que la aplicación esté corriendo, puedes acceder a la documentación interactiva de la API para ver todos los detalles y probar los endpoints directamente desde tu navegador.

* **URL de Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## ? Calidad de Código

### Pruebas
El proyecto está configurado con pruebas unitarias para la lógica de negocio, alcanzando una **cobertura superior al 90%** en los casos de uso. Para ejecutar todas las pruebas, usa el comando:

```bash
./gradlew test
```

### Análisis Estático de Código
Se recomienda el uso del plugin **SonarLint** en el IDE (IntelliJ IDEA) para la validación y mejora continua de la calidad del código en tiempo de desarrollo.