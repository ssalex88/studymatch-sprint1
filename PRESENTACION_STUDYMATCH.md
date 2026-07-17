# Guía de presentación — StudyMatch Sprint 1

Esta guía es para explicar StudyMatch en una presentación: qué problema resuelve, cómo fluye la aplicación, qué hace cada parte del código y cómo defender las decisiones técnicas si preguntan.

> Nota: este archivo es material personal de presentación. No hace falta commitearlo.

---

## 1. Resumen corto para abrir la presentación

StudyMatch es una aplicación web para estudiantes que quieren encontrar compañeros de estudio y organizar su perfil académico.

En el Sprint 1 implementamos la base funcional del sistema:

- Registro de usuarios.
- Inicio y cierre de sesión.
- Perfil académico editable.
- Panel administrativo para gestionar roles.
- Backend con autenticación real del lado servidor.
- Base de datos MySQL.
- Docker Compose para levantar frontend, backend y base de datos con un solo comando.

La arquitectura está separada en tres capas:

```text
React/Vite Frontend  ->  Java HttpServer Backend  ->  MySQL 8
localhost:5173       ->  localhost:8080/api       ->  localhost:3307
```

La idea importante: el frontend muestra la interfaz, pero la seguridad y las reglas importantes las decide el backend.

---

## 2. Cómo levantar el proyecto para la demo

Desde la raíz del repositorio:

```bash
docker compose up --build
```

Después abrir:

```text
http://localhost:5173
```

Docker levanta tres servicios:

| Servicio | Qué hace | URL/Puerto |
| --- | --- | --- |
| `frontend` | Interfaz React con Vite | `http://localhost:5173` |
| `backend` | API Java con `HttpServer` nativo | `http://localhost:8080/api` |
| `mysql` | Base de datos MySQL 8 | host: `localhost:3307`, contenedor: `mysql:3306` |

Para apagar sin borrar datos:

```bash
docker compose down
```

Para apagar y resetear la base Docker:

```bash
docker compose down -v
```

---

## 3. Qué no necesitan instalar si usan Docker

Si levantan el proyecto con Docker Compose, no necesitan instalar localmente:

- Java.
- Maven.
- Node.js.
- MySQL.

Sí necesitan:

- Docker Desktop o Docker Engine.
- Docker Compose.

¿Por qué?

- El backend se compila dentro de una imagen Maven + Java 21.
- El backend corre dentro de una imagen Java 21 JRE.
- El frontend corre dentro de una imagen Node.
- MySQL corre dentro de un contenedor MySQL 8.

---

## 4. Flujo general de la aplicación

### 4.1 Registro

1. El usuario entra a `/registrar`.
2. Completa nombre, correo institucional, contraseña, carrera y ciclo.
3. El frontend llama al backend:

```http
POST /api/auth/registrar
```

1. El backend valida datos básicos.
2. El backend revisa que el correo no exista.
3. La contraseña se guarda hasheada con PBKDF2.
4. El usuario queda registrado en MySQL.
5. El backend crea una sesión y la envía como cookie `HttpOnly`.
6. El frontend guarda solo datos no sensibles para la UI.
7. El usuario entra a la app.

Idea para explicar:

> El registro no solo guarda datos; también crea una sesión backend para que el usuario quede autenticado sin guardar tokens sensibles en `localStorage`.

---

### 4.2 Login

1. El usuario entra a `/login`.
2. Escribe correo y contraseña.
3. El frontend llama:

```http
POST /api/auth/login
```

1. El backend busca el usuario por correo.
2. Verifica la contraseña.
3. Si es correcta, crea una sesión en memoria.
4. Devuelve una cookie `HttpOnly` llamada `studyMatchSession`.
5. El navegador manda esa cookie automáticamente en siguientes requests.

Punto de defensa:

> La autenticación no depende de confiar en el frontend. Aunque alguien modifique `localStorage`, el backend exige una sesión válida para acceder a rutas protegidas.

---

### 4.3 Perfil

1. El usuario autenticado entra a `/perfil`.
2. El frontend lee el usuario cacheado solo para mostrar la pantalla rápido.
3. Después consulta al backend:

```http
GET /api/usuarios/{idUsuario}
```

1. El backend verifica la cookie de sesión.
2. El backend valida que el usuario pueda ver ese perfil:
   - puede ver su propio perfil;
   - o puede verlo si es administrador.
3. El usuario puede actualizar carrera, ciclo, código de alumno y biografía:

```http
PATCH /api/usuarios/{idUsuario}/perfil
```

1. El backend persiste los cambios en MySQL.

Punto de defensa:

> El frontend ayuda a navegar, pero el backend vuelve a validar permisos antes de leer o modificar un perfil.

---

### 4.4 Administración de roles

1. Un usuario administrador entra a `/admin`.
2. El frontend pide la lista de usuarios:

```http
GET /api/usuarios
```

1. El backend verifica sesión.
2. El backend verifica que el usuario autenticado sea admin.
3. El admin puede cambiar roles:

```http
PATCH /api/usuarios/{idUsuario}/rol
```

1. El backend actualiza el rol en MySQL.

Punto de defensa:

> El rol de administrador no se decide en la interfaz. El backend consulta la sesión real y el usuario real en base de datos antes de permitir la operación.

---

### 4.5 Logout

1. El usuario presiona cerrar sesión.
2. El frontend llama:

```http
POST /api/auth/logout
```

1. El backend elimina la sesión del servidor.
2. El backend borra la cookie.
3. El frontend limpia el cache visual del usuario.
4. El usuario vuelve a `/login`.

Punto de defensa:

> Logout invalida la sesión del lado servidor; no es solo borrar datos del navegador.

---

## 5. Arquitectura técnica

```text
studymatch-frontend/
  React + Vite
  Pantallas, navegación y llamadas HTTP

studymatch-backend/
  Java 21
  HttpServer nativo
  Controladores, servicios, repositorios y configuración

studymatch-database/
  schema.sql
  Tablas MySQL del sistema

docker-compose.yml
  Orquesta MySQL, backend y frontend
```

Separación de responsabilidades:

| Capa | Responsabilidad |
| --- | --- |
| Frontend | Mostrar UI, capturar formularios, llamar API, navegar entre pantallas. |
| Backend Controller | Recibir requests HTTP, validar entrada básica, devolver respuestas HTTP. |
| Backend Service | Aplicar reglas de negocio: registro, login, permisos, hashing, sesiones. |
| Backend Repository | Ejecutar SQL contra MySQL. |
| Database | Persistir usuarios, horarios, círculos y membresías. |
| Docker Compose | Levantar todo el entorno reproducible. |

---

## 6. Backend explicado por archivos

### `App.java`

Es el punto de entrada del backend.

Hace esto:

1. Crea un servidor HTTP en el puerto `8080`.
2. Crea los servicios principales.
3. Registra rutas:
   - `/api/auth`
   - `/api/usuarios`
4. Arranca el servidor.

Si preguntan qué hace:

> `App.java` inicializa el backend. Es como el main del servidor: crea dependencias, registra endpoints y deja la API escuchando.

---

### `AuthController.java`

Maneja autenticación.

Endpoints principales:

| Método | Ruta | Qué hace |
| --- | --- | --- |
| `POST` | `/api/auth/registrar` | Registra usuario y crea sesión. |
| `POST` | `/api/auth/login` | Valida credenciales y crea sesión. |
| `POST` | `/api/auth/logout` | Elimina sesión y borra cookie. |

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `handle` | Punto central: decide qué acción ejecutar según método y ruta. |
| `manejarRegistro` | Procesa registro de usuario. |
| `manejarLogin` | Procesa login. |
| `manejarLogout` | Procesa logout. |
| `validarRegistro` | Valida campos obligatorios del registro. |
| `validarLogin` | Valida campos obligatorios del login. |
| `adjuntarCookieSesion` | Agrega la cookie `HttpOnly` al response. |
| `limpiarCookieSesion` | Borra la cookie al cerrar sesión. |
| `leerCuerpo` | Convierte el JSON recibido en DTO Java. |
| `enviarRespuesta` / `enviarError` | Devuelven respuestas HTTP en JSON. |

Idea clave:

> Este controller no guarda directamente en base de datos. Delegar eso al service mantiene el código separado y más fácil de defender.

---

### `UsuarioController.java`

Maneja operaciones sobre usuarios.

Endpoints principales:

| Método | Ruta | Qué hace |
| --- | --- | --- |
| `GET` | `/api/usuarios` | Lista usuarios. Requiere admin. |
| `GET` | `/api/usuarios/{id}` | Obtiene usuario por id. Requiere ser dueño o admin. |
| `PATCH` | `/api/usuarios/{id}/perfil` | Actualiza perfil académico. Requiere ser dueño o admin. |
| `PATCH` | `/api/usuarios/{id}/rol` | Cambia rol. Requiere admin. |

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `handle` | Decide qué endpoint ejecutar según método y path. |
| `manejarListarUsuarios` | Devuelve todos los usuarios, si el usuario actual es admin. |
| `manejarObtenerUsuario` | Devuelve un usuario específico si hay permiso. |
| `manejarActualizarPerfil` | Actualiza carrera, ciclo, código y biografía. |
| `manejarCambioDeRol` | Cambia el rol de un usuario. |
| `requerirUsuarioAutenticado` | Lee la cookie/sesión y obtiene el usuario real. |
| `puedeGestionarUsuario` | Permite operar si es el mismo usuario o admin. |
| `esAdministrador` | Valida si el rol es admin. |

Idea clave:

> Este controller es donde se ve que la seguridad está en backend: antes de responder o modificar, consulta la sesión real.

---

### `UsuarioService.java`

Contiene reglas de negocio de usuario.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `registrarUsuario` | Valida duplicados, hashea contraseña, crea usuario y devuelve DTO seguro. |
| `iniciarSesion` | Busca usuario, verifica contraseña y devuelve datos seguros. |
| `listarTodosLosUsuarios` | Pide todos los usuarios al repository. |
| `modificarPerfil` | Actualiza datos académicos. |
| `obtenerUsuarioPorId` | Busca usuario por id. |
| `cambiarRolUsuario` | Cambia el rol del usuario. |
| `cifrarContrasena` | Genera hash PBKDF2 para nuevas contraseñas. |
| `verificarContrasena` | Detecta formato PBKDF2 o SHA-256 legacy y verifica. |
| `actualizarHashLegacySiCorresponde` | Si un usuario viejo tenía SHA-256, al loguearse se migra a PBKDF2. |

Punto fuerte para defender:

> Las contraseñas nuevas no se guardan en texto plano ni con SHA simple. Se usa PBKDF2 con salt e iteraciones. Además hay compatibilidad con hashes viejos y migración automática al login.

---

### `SessionService.java`

Administra sesiones del lado servidor.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `crearSesion` | Genera token seguro y guarda la sesión en memoria. |
| `eliminarSesion` | Borra una sesión al hacer logout. |
| `obtenerUsuarioAutenticado` | Lee cookie o bearer token, valida sesión y devuelve usuario. |
| `obtenerTokenDeSesion` | Extrae el token desde cookie o header. |
| `extraerTokenDeCookie` | Busca `studyMatchSession` en cookies HTTP. |
| `extraerBearerToken` | Permite compatibilidad con header `Authorization: Bearer`. |
| `estaExpirada` | Verifica si la sesión venció. |

Punto de defensa:

> El token no vive como autoridad en `localStorage`. Vive como cookie `HttpOnly` y el backend mantiene el estado de sesión.

Limitación honesta:

> Para Sprint 1, las sesiones están en memoria. Eso está bien para demo/local, pero en producción se movería a Redis, base de datos o un mecanismo distribuido.

---

### `UsuarioRepository.java`

Es la capa que habla con MySQL.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `guardar` | Inserta un usuario nuevo. |
| `buscarPorCorreo` | Busca usuario por email institucional. |
| `listarTodos` | Lista usuarios. |
| `buscarPorId` | Busca usuario específico. |
| `actualizarPerfil` | Actualiza datos académicos. |
| `actualizarRol` | Cambia rol. |
| `actualizarContrasena` | Actualiza hash de contraseña, usado para migración legacy. |
| `mapearUsuario` | Convierte un `ResultSet` SQL en objeto `Usuario`. |
| `mapearUsuarioSinContrasena` | Evita exponer contraseña cuando no hace falta. |

Punto de defensa:

> El repository usa `PreparedStatement`, que evita concatenar SQL con datos del usuario y reduce riesgo de inyección SQL.

---

### `DatabaseConfig.java`

Configura la conexión a MySQL.

Puede usar variables de entorno:

```text
DB_URL
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
```

Si no se definen, usa valores locales por defecto:

```text
localhost:3306
studymatch_db
studymatch_user
123456
```

En Docker, el backend usa:

```text
DB_HOST=mysql
DB_PORT=3306
```

Punto clave:

> Dentro de Docker, `localhost` no sería la base de datos; sería el propio contenedor backend. Por eso se usa el nombre del servicio `mysql`.

---

### `CorsHelper.java`

Agrega headers CORS a las respuestas.

Permite que el frontend en `localhost:5173` llame al backend en `localhost:8080` con cookies.

Headers importantes:

```text
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Credentials: true
```

Punto de defensa:

> Como usamos cookies, no podemos permitir origen wildcard `*`. Debe ser un origen explícito.

---

## 7. Frontend explicado por archivos

### `App.jsx`

Define las rutas de React.

Rutas:

| Ruta | Pantalla |
| --- | --- |
| `/login` | Login |
| `/registrar` | Registro |
| `/perfil` | Perfil académico |
| `/horario` | Horario, preparado para siguientes HU |
| `/circulos` | Círculos, preparado para siguientes HU |
| `/admin` | Panel admin |

También redirige `/` y rutas desconocidas a `/login`.

Idea clave:

> `App.jsx` organiza la navegación. Las rutas privadas usan `Layout` para compartir menú lateral.

---

### `Login.jsx`

Pantalla de inicio de sesión.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `handleChange` | Actualiza el estado del formulario cuando el usuario escribe. |
| `handleSubmit` | Llama `loginUser`, guarda cache visual y navega a perfil. |

Flujo:

```text
Formulario -> authService.loginUser -> backend /api/auth/login -> cookie HttpOnly -> /perfil
```

---

### `Registro.jsx`

Pantalla de registro.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `handleChange` | Actualiza los campos del formulario. |
| `handleSubmit` | Llama `registerUser`, maneja errores y navega si el registro funciona. |

Flujo:

```text
Formulario -> authService.registerUser -> backend /api/auth/registrar -> usuario en MySQL -> sesión -> /perfil
```

---

### `Perfil.jsx`

Pantalla de perfil académico.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `mapearPerfilAFormulario` | Convierte los datos del usuario al formato del formulario. |
| `handleChange` | Actualiza campos del formulario. |
| `handleSubmit` | Envía actualización al backend. |

Flujo:

```text
Carga perfil -> GET /api/usuarios/{id}
Edita perfil -> PATCH /api/usuarios/{id}/perfil
```

Punto de defensa:

> El perfil no se actualiza solamente en pantalla. Se persiste en MySQL a través del backend.

---

### `AdminDashboard.jsx`

Panel para administración de usuarios y roles.

Funciones importantes:

| Función | Qué hace |
| --- | --- |
| `cargarUsuarios` | Llama al backend para traer todos los usuarios. |
| `handleCambiarRol` | Envía el nuevo rol al backend. |
| `obtenerClasesRol` | Define estilos visuales para el badge del rol. |

Flujo:

```text
Admin entra -> GET /api/usuarios -> lista usuarios
Cambia rol -> PATCH /api/usuarios/{id}/rol
```

Punto de defensa:

> Aunque el botón exista en frontend, el backend igual valida que el usuario autenticado sea administrador.

---

### `Layout.jsx`

Es el contenedor visual de las páginas privadas.

Hace:

- Menú lateral.
- Navegación entre perfil, horario, círculos y admin.
- Botón de cerrar sesión.

Función importante:

| Función | Qué hace |
| --- | --- |
| `handleCerrarSesion` | Llama logout backend, limpia cache y navega a login. |

---

### `authService.js`

Centraliza llamadas de autenticación al backend.

Funciones:

| Función | Endpoint | Qué hace |
| --- | --- | --- |
| `registerUser` | `POST /api/auth/registrar` | Registra usuario. |
| `loginUser` | `POST /api/auth/login` | Inicia sesión. |
| `logoutUser` | `POST /api/auth/logout` | Cierra sesión. |

Todas usan:

```js
credentials: "include"
```

Eso es importante porque permite que el navegador mande y reciba cookies.

---

### `userService.js`

Centraliza llamadas de usuarios.

Funciones:

| Función | Endpoint | Qué hace |
| --- | --- | --- |
| `getAllUsers` | `GET /api/usuarios` | Lista usuarios para admin. |
| `getUserById` | `GET /api/usuarios/{id}` | Obtiene perfil. |
| `updateUserProfile` | `PATCH /api/usuarios/{id}/perfil` | Actualiza perfil. |
| `updateUserRole` | `PATCH /api/usuarios/{id}/rol` | Cambia rol. |

También usa `credentials: "include"` para mandar la cookie de sesión.

---

### `currentUserCache.js`

Guarda datos mínimos del usuario en `localStorage` para mejorar la experiencia visual.

Funciones:

| Función | Qué hace |
| --- | --- |
| `cacheCurrentUserForUi` | Guarda datos no sensibles del usuario. |
| `clearCurrentUserCache` | Borra cache al cerrar sesión. |

Punto importante:

> Este cache no es autoridad de seguridad. Sirve para la UI. La autorización real la decide el backend con la cookie y la sesión.

---

## 8. Base de datos

Archivo:

```text
studymatch-database/schema.sql
```

Tablas:

| Tabla | Qué guarda |
| --- | --- |
| `usuarios` | Datos de usuarios, rol, contraseña hasheada y perfil académico. |
| `horario_disponibilidad` | Disponibilidad horaria por usuario, preparado para HU futuras. |
| `circulo_estudio` | Círculos de estudio, preparado para HU futuras. |
| `miembrecia_circulo` | Relación muchos-a-muchos entre usuarios y círculos. |

Tabla principal del Sprint 1:

```text
usuarios
```

Campos importantes:

| Campo | Uso |
| --- | --- |
| `id_usuario` | Identificador único. |
| `nombre_completo` | Nombre del estudiante. |
| `correo_institucional` | Login único. |
| `codigo_alumno` | Dato académico del perfil. |
| `contrasena` | Hash de contraseña, no texto plano. |
| `rol` | Control de permisos. |
| `carrera` | Perfil académico. |
| `ciclo` | Perfil académico. |
| `biografia` | Perfil personal/académico. |

---

## 9. Docker explicado

Archivo principal:

```text
docker-compose.yml
```

Servicios:

### `mysql`

- Usa imagen `mysql:8.0`.
- Crea base `studymatch_db`.
- Crea usuario `studymatch_user`.
- Carga `schema.sql` automáticamente.
- Expone MySQL al host en `localhost:3307`.

### `backend`

- Se construye desde `studymatch-backend/Dockerfile`.
- Usa Java 21.
- Se conecta a MySQL usando `DB_HOST=mysql`.
- Expone API en `localhost:8080`.
- Espera a que MySQL esté healthy antes de arrancar.

### `frontend`

- Se construye desde `studymatch-frontend/Dockerfile`.
- Usa Node 22 Alpine.
- Corre Vite en `0.0.0.0:5173`.
- Expone la app en `localhost:5173`.

Pregunta típica:

> ¿Por qué el frontend usa `localhost:8080/api` y no `backend:8080`?

Respuesta:

> Porque el código React corre en el navegador del usuario. Desde el navegador, `backend` no existe como DNS de Docker. El navegador accede por los puertos publicados del host, por eso usa `localhost:8080/api`.

---

## 10. Seguridad y decisiones defendibles

### 10.1 Contraseñas

No se guardan en texto plano.

Se usa PBKDF2:

```text
PBKDF2$iterations$saltBase64$hashBase64
```

Qué significa:

- `iterations`: cantidad de rondas para hacer el cálculo más costoso.
- `salt`: valor aleatorio para que dos contraseñas iguales no generen el mismo hash.
- `hash`: resultado final que se guarda.

Respuesta si preguntan:

> Usamos PBKDF2 porque es una función de derivación de claves pensada para contraseñas. Es más defendible que SHA-256 simple.

---

### 10.2 Sesiones

La sesión se maneja con cookie `HttpOnly`:

```text
studyMatchSession
```

Ventaja:

- JavaScript del frontend no puede leer esa cookie.
- Reduce exposición ante XSS comparado con guardar token en `localStorage`.
- El backend valida la sesión antes de responder rutas protegidas.

Limitación honesta:

> En Sprint 1 las sesiones están en memoria. Si se reinicia el backend, se pierden. Para producción se usaría un almacenamiento persistente o distribuido.

---

### 10.3 Autorización backend

El backend protege:

- Listado de usuarios: solo admin.
- Cambio de rol: solo admin.
- Ver perfil: dueño o admin.
- Editar perfil: dueño o admin.

Respuesta si preguntan:

> No confiamos en que el frontend oculte botones. La regla real vive en el backend.

---

### 10.4 CORS con credenciales

Como usamos cookies, CORS debe permitir credenciales:

```text
Access-Control-Allow-Credentials: true
```

Y el origen debe ser explícito:

```text
http://localhost:5173
```

No se usa `*` porque con cookies eso sería inseguro e inválido para este caso.

---

## 11. Endpoints principales para mencionar

| Endpoint | Método | Auth | Descripción |
| --- | --- | --- | --- |
| `/api/auth/registrar` | `POST` | No | Crea usuario y sesión. |
| `/api/auth/login` | `POST` | No | Inicia sesión. |
| `/api/auth/logout` | `POST` | Sí | Cierra sesión. |
| `/api/usuarios` | `GET` | Admin | Lista usuarios. |
| `/api/usuarios/{id}` | `GET` | Dueño/Admin | Obtiene perfil. |
| `/api/usuarios/{id}/perfil` | `PATCH` | Dueño/Admin | Actualiza perfil. |
| `/api/usuarios/{id}/rol` | `PATCH` | Admin | Cambia rol. |

---

## 12. Guion recomendado para presentar

### Inicio

> StudyMatch busca ayudar a estudiantes a conectarse para estudiar. En este Sprint 1 nos enfocamos en la base: usuarios, autenticación, perfil académico, roles y despliegue local reproducible con Docker.

### Arquitectura

> La app está dividida en frontend React, backend Java nativo con HttpServer y MySQL. Docker Compose levanta las tres capas juntas, así cualquier persona puede probar el sistema sin instalar Java, Node ni MySQL localmente.

### Demo funcional

1. Abrir `http://localhost:5173`.
2. Registrar usuario.
3. Mostrar que entra al perfil.
4. Editar perfil académico.
5. Cerrar sesión.
6. Volver a iniciar sesión.
7. Si hay usuario admin disponible, mostrar panel admin y roles.

### Seguridad

> El punto más importante es que la autenticación no es solo visual. El backend crea sesiones, guarda la cookie HttpOnly, valida cada request protegida y aplica permisos por rol.

### Docker

> Para levantar todo basta `docker compose up --build`. MySQL carga el schema automáticamente, el backend se conecta al servicio `mysql`, y el frontend consume la API por `localhost:8080/api`.

---

## 13. Preguntas probables y respuestas

### ¿Por qué usaron Java `HttpServer` nativo y no Spring Boot?

Porque el requerimiento del Sprint era mantener un backend liviano sin framework pesado. `HttpServer` nos permite controlar explícitamente rutas, requests, respuestas y dependencias.

---

### ¿Dónde se valida la seguridad?

En el backend:

- `SessionService` valida sesión.
- `UsuarioController` valida permisos.
- `UsuarioService` valida contraseña y reglas de usuario.

El frontend solo mejora la experiencia, pero no decide permisos finales.

---

### ¿Por qué `localStorage` si dicen que no confían en frontend?

Se usa solo como cache visual de datos no sensibles. No guarda el token de sesión ni autoriza acciones. Si alguien modifica `localStorage`, el backend igual rechaza requests sin cookie válida.

---

### ¿Qué pasa si reinicio el backend?

Las sesiones en memoria se pierden y el usuario debe iniciar sesión otra vez. Es una limitación aceptada para Sprint 1. En producción se usaría Redis, base de datos o sesiones persistentes.

---

### ¿La contraseña se puede recuperar desde la base?

No. La base guarda un hash PBKDF2, no la contraseña original. El login compara la contraseña ingresada calculando el hash correspondiente.

---

### ¿Por qué MySQL está en `3307` y no `3306`?

Dentro de Docker MySQL usa `3306`, pero hacia la máquina host se publica como `3307` para evitar choque con un MySQL local ya instalado.

---

### ¿Por qué el backend usa `mysql` como host?

Porque en Docker Compose los servicios se resuelven por nombre. El backend no debe usar `localhost` para la base, porque `localhost` sería el propio contenedor backend.

---

### ¿Qué evidencia tienen de que funciona?

Se verificó:

- `mvn clean package`.
- `npm run build`.
- `npm run lint`.
- `docker compose config`.
- `docker compose up --build -d`.
- Frontend HTTP 200.
- Backend protegido HTTP 401 sin sesión.
- Registro HTTP 201.
- Perfil autenticado HTTP 200.
- Tablas MySQL creadas desde `schema.sql`.

---

## 14. Mapa mental para recordar funciones

Si te preguntan por una función, ubicála en este patrón:

```text
Controller = HTTP
Service    = reglas de negocio
Repository = SQL/MySQL
Config     = entorno/conexión/CORS
Frontend service = llamadas fetch
Component  = pantalla/eventos del usuario
```

Ejemplos:

| Si preguntan por... | Respondé... |
| --- | --- |
| `handle` en controller | Es el dispatcher HTTP: mira método/ruta y llama la función correcta. |
| `manejarLogin` | Procesa login desde HTTP y delega validación al service. |
| `iniciarSesion` | Regla de negocio: buscar usuario y verificar contraseña. |
| `crearSesion` | Genera token y lo guarda como sesión activa. |
| `getConnection` | Abre conexión JDBC con MySQL usando configuración/env vars. |
| `getUserById` frontend | Hace fetch al backend para traer perfil. |
| `handleSubmit` en React | Maneja envío de formulario. |
| `updateUserProfile` | Envía PATCH para guardar perfil en backend. |

---

## 15. Cierre recomendado

> En conclusión, Sprint 1 deja una base funcional y defendible: usuarios reales en MySQL, autenticación backend con cookie HttpOnly, contraseñas hasheadas con PBKDF2, permisos por rol, perfil editable y una forma reproducible de levantar todo con Docker. Lo que queda para siguientes sprints es avanzar sobre horarios, círculos de estudio y matching entre estudiantes.
