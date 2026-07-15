# Software Design Document (SDD)

## StudyMatch — Sprint 1: Registro y autenticación de usuarios

**Versión:** 1.0
**Estado:** Documento base para implementación
**Sistema:** StudyMatch
**Sprint:** Sprint 1
**Tecnologías:** React, Vite, Tailwind CSS, Java 21, servidor HTTP nativo y MySQL 8.x

---

# 1. Propósito

El presente Software Design Document define el diseño funcional y técnico del Sprint 1 de StudyMatch, denominado **“Registro y autenticación de usuarios”**.

El documento organiza en una sola especificación:

- el objetivo del Sprint 1;
- las historias de usuario aprobadas;
- los casos de uso representados en los diagramas UML;
- las interacciones descritas en los diagramas de secuencia;
- las clases y relaciones del modelo de dominio;
- el modelo de datos;
- la arquitectura de despliegue;
- las responsabilidades del frontend, backend y base de datos;
- las tareas de implementación;
- los criterios de aceptación;
- las condiciones de verificación;
- la Definition of Done.

El SDD no incorpora nuevas historias de usuario ni amplía el alcance funcional aprobado. Los elementos generales del sistema que aparecen en los diagramas, pero no forman parte de las historias del Sprint 1, se conservan únicamente como contexto del diseño global de StudyMatch.

---

# 2. Alcance

## 2.1 Alcance funcional del Sprint 1

El Sprint 1 comprende las siguientes funcionalidades:

1. Registro de estudiantes.
2. Inicio de sesión de usuarios.
3. Visualización y edición del perfil académico.
4. Administración de usuarios y roles.

Las funcionalidades se encuentran asociadas a las siguientes historias de usuario:

- **HU1:** Registrar estudiante.
- **HU2:** Iniciar sesión.
- **HU3:** Gestionar perfil académico.
- **HU4:** Administrar usuarios y roles.

## 2.2 Elementos representados como soporte

El diagrama de casos de uso y el diagrama de secuencia incluyen el flujo de restablecimiento de contraseña y envío de enlace. Este flujo se conserva como una interacción complementaria del módulo de autenticación, pero no se establece como una historia de usuario adicional ni modifica el backlog aprobado del Sprint 1.

## 2.3 Elementos del modelo general fuera del alcance funcional

Los diagramas de clases y de datos muestran entidades que corresponden al modelo general de StudyMatch:

- Horario.
- Participación.
- Círculo de estudio.
- Sesión de estudio.
- Asistencia.
- Reserva.

Estas entidades se mantienen como parte de la arquitectura global del sistema, pero su funcionalidad completa no forma parte de las cuatro historias del Sprint 1.

---

# 3. Contexto del sistema

StudyMatch es una plataforma orientada a estudiantes que permite gestionar usuarios, perfiles académicos, disponibilidad horaria y participación en círculos de estudio.

El Sprint 1 establece la base de identificación y control de acceso del sistema. Antes de que un usuario pueda participar en círculos de estudio, registrar asistencia o utilizar las demás funcionalidades de StudyMatch, debe existir un mecanismo que permita:

- crear una cuenta;
- identificar al usuario;
- validar sus credenciales;
- mantener actualizada su información;
- controlar el acceso según su rol.

El módulo de usuarios y autenticación constituye la base para los siguientes sprints del proyecto.

---

# 4. Objetivo del Sprint 1

Implementar el acceso seguro a la plataforma mediante el registro, autenticación y administración de usuarios, garantizando la protección de la información y el control de acceso según los roles definidos.

---

# 5. Historias de usuario

## 5.1 HU1 — Registrar estudiante

**Como estudiante**, quiero registrarme con mi correo institucional y datos académicos, para acceder a la plataforma de forma segura.

## 5.2 HU2 — Iniciar sesión

**Como usuario**, quiero iniciar sesión de manera segura, para acceder a las funcionalidades según mi rol.

## 5.3 HU3 — Gestionar perfil académico

**Como usuario**, quiero visualizar y editar mi perfil académico, para mantener mi información actualizada.

## 5.4 HU4 — Administrar usuarios y roles

**Como administrador**, quiero validar usuarios y asignar roles, para controlar el acceso al sistema.

---

# 6. Actores del sistema

## 6.1 Estudiante

Actor que puede registrarse, iniciar sesión y gestionar su perfil académico.

## 6.2 Usuario

Representa a cualquier persona registrada en StudyMatch. Según el modelo de clases, un usuario puede tener uno de los siguientes roles:

- ADMINISTRADOR.
- PROFESOR.
- ORGANIZADOR.
- ESTUDIANTE.

## 6.3 Administrador

Actor responsable de gestionar usuarios y modificar sus roles dentro del sistema.

## 6.4 StudyMatch

Representa la aplicación que procesa las acciones de registro, autenticación, perfil y administración de usuarios.

---

# 7. Casos de uso del Sprint 1

## 7.1 Registrar usuario

Permite que un estudiante complete el proceso de registro proporcionando sus datos personales, académicos y credenciales.

### Actor principal

Estudiante.

### Flujo relacionado

- Registrar usuario.
- Completar registro.

### Resultado esperado

El usuario queda registrado en StudyMatch con la información ingresada.

---

## 7.2 Iniciar sesión

Permite que un usuario ingrese sus credenciales y acceda al sistema.

### Actor principal

Usuario.

### Flujo relacionado

- Iniciar sesión.
- Ingresar.

### Resultado esperado

El sistema valida las credenciales y permite el acceso a las funcionalidades correspondientes al rol del usuario.

---

## 7.3 Restablecer contraseña

Permite iniciar un proceso de recuperación de acceso cuando el usuario necesita cambiar o restablecer su contraseña.

### Actor principal

Usuario.

### Flujo relacionado

- Restablecer contraseña.
- Enviar enlace.

### Resultado esperado

El sistema valida la existencia del usuario y procesa el envío del enlace indicado en el diagrama.

### Consideración de alcance

Este flujo se documenta porque aparece en los diagramas UML proporcionados. No se convierte en una historia de usuario adicional.

---

## 7.4 Gestionar perfil académico

Permite que un usuario consulte y edite la información asociada a su perfil.

### Actor principal

Usuario.

### Flujo relacionado

- Gestionar perfil académico.
- Editar datos.
- Actualizar perfil.

### Resultado esperado

El sistema muestra la información del perfil y permite actualizar los datos permitidos.

---

## 7.5 Administrar usuarios y roles

Permite que un administrador seleccione usuarios y modifique sus roles.

### Actor principal

Administrador.

### Flujo relacionado

- Administrar usuarios y roles.
- Seleccionar usuario.
- Alternar rol.

### Resultado esperado

El sistema actualiza el rol del usuario seleccionado.

---

# 8. Arquitectura del sistema

## 8.1 Estilo arquitectónico

StudyMatch utiliza una arquitectura monolítica organizada en tres capas principales:

```text
Frontend React
      ↓ HTTP
Backend Java 21
      ↓ JDBC
Base de datos MySQL 8.x
```

La solución se mantiene como un solo sistema, pero separa claramente las responsabilidades entre interfaz, lógica de negocio y persistencia.

## 8.2 Capas principales

### Frontend

Responsable de:

- mostrar formularios;
- capturar datos del usuario;
- ejecutar validaciones visuales;
- consumir los servicios del backend;
- mostrar respuestas y mensajes;
- controlar la navegación de la interfaz.

### Backend

Responsable de:

- recibir solicitudes HTTP;
- procesar la lógica de registro y autenticación;
- gestionar usuarios y roles;
- validar datos;
- coordinar el acceso a la base de datos;
- devolver respuestas al frontend.

### Base de datos

Responsable de:

- almacenar usuarios;
- mantener las relaciones definidas por el modelo;
- conservar los datos del perfil;
- guardar roles, estados y credenciales;
- almacenar las entidades generales de StudyMatch.

---

# 9. Diseño del backend

## 9.1 Organización por capas

El backend mantiene la siguiente separación:

```text
controller/
dto/
entity/
repository/
service/
config/
```

## 9.2 Responsabilidades

### Controller

- Recibe solicitudes HTTP.
- Obtiene los datos enviados por el frontend.
- Invoca el servicio correspondiente.
- Devuelve la respuesta al cliente.

### DTO

- Representa los datos que ingresan o salen del backend.
- Evita que la interfaz dependa directamente de las entidades de persistencia.
- Organiza los datos de registro, inicio de sesión, perfil y administración.

### Entity

- Representa las clases del dominio.
- Mantiene correspondencia con las entidades y tablas del sistema.
- Contiene los atributos definidos en los diagramas.

### Repository

- Ejecuta operaciones de acceso a datos.
- Consulta usuarios.
- Registra nuevos usuarios.
- Actualiza perfiles.
- Actualiza roles.
- Persiste la información en MySQL.

### Service

- Contiene la lógica de negocio.
- Valida los datos.
- Coordina las operaciones entre controller y repository.
- Aplica las reglas de registro, autenticación, perfil y administración.

### Config

- Centraliza la configuración técnica del backend.
- Incluye parámetros de conexión y configuración del servidor.

---

# 10. Diseño del frontend

## 10.1 Tecnologías

- React.
- Vite.
- Tailwind CSS.

## 10.2 Organización general

El frontend se organiza mediante:

```text
components/
config/
core/services/
features/
```

## 10.3 Responsabilidades

### Components

Contiene elementos reutilizables de la interfaz.

### Config

Centraliza configuraciones necesarias para la ejecución del frontend, incluyendo las direcciones de los servicios.

### Core services

Contiene la comunicación HTTP con el backend.

### Features

Agrupa las pantallas y componentes según la funcionalidad:

- registro;
- inicio de sesión;
- perfil;
- administración.

## 10.4 Pantallas del Sprint 1

El Sprint 1 debe considerar las pantallas correspondientes a:

1. Registro.
2. Inicio de sesión.
3. Perfil académico.
4. Administración de usuarios y roles.
5. Recuperación de contraseña, únicamente como flujo representado en los diagramas.

Los componentes en React deben respetar los mockups aprobados por el Product Owner y visualizarse correctamente en distintos tamaños de pantalla y navegadores modernos.

---

# 11. Diseño de secuencias

## 11.1 Secuencia de registro

### Participantes

- Usuario.
- FrmRegistro.
- UsuarioService.
- UsuarioDAO.

### Flujo

1. El usuario solicita el registro.
2. `FrmRegistro` muestra el formulario.
3. El usuario envía sus datos.
4. `FrmRegistro` solicita completar el registro.
5. `UsuarioService` procesa la información.
6. `UsuarioService` solicita guardar al usuario.
7. `UsuarioDAO` registra la información.
8. `UsuarioDAO` confirma que el usuario fue guardado.
9. `UsuarioService` confirma el registro exitoso.
10. `FrmRegistro` muestra el mensaje de éxito.

### Resultado

El estudiante queda registrado en el sistema.

---

## 11.2 Secuencia de inicio de sesión

### Participantes

- Usuario.
- AppStudyMatch.
- UsuarioService.
- UsuarioDAO.

### Flujo

1. El usuario ingresa correo y contraseña.
2. `AppStudyMatch` envía las credenciales.
3. `UsuarioService` solicita consultar al usuario.
4. `UsuarioDAO` realiza la búsqueda.
5. `UsuarioDAO` devuelve el usuario encontrado.
6. `UsuarioService` valida las credenciales.
7. `AppStudyMatch` informa el inicio de sesión exitoso.
8. El usuario accede al sistema.

### Resultado

El usuario puede ingresar a las funcionalidades correspondientes a su rol.

---

## 11.3 Secuencia de recuperación de contraseña

### Participantes

- Usuario.
- FrmRecuperarContraseña.
- ServicioCorreo.
- UsuarioDAO.

### Flujo representado

1. El usuario solicita el cambio o restauración de contraseña.
2. El formulario solicita validar las credenciales existentes.
3. `UsuarioDAO` consulta la existencia del usuario.
4. `UsuarioDAO` devuelve el usuario encontrado.
5. El servicio de correo procesa el envío del código o enlace.
6. El formulario permite continuar con el restablecimiento.

### Resultado

El usuario recibe el mecanismo de recuperación representado en el diagrama.

### Consideración

Este flujo se mantiene como parte del diseño existente y no modifica las cuatro historias de usuario aprobadas.

---

## 11.4 Secuencia de perfil académico

### Participantes

- Usuario.
- AppStudyMatch.
- UsuarioService.
- UsuarioDAO.

### Flujo

1. El usuario abre el perfil académico.
2. `AppStudyMatch` solicita obtener los datos del perfil.
3. `UsuarioService` devuelve los datos.
4. `AppStudyMatch` muestra el perfil.
5. El usuario edita sus datos.
6. `AppStudyMatch` solicita actualizar el perfil.
7. `UsuarioService` procesa la actualización.
8. `UsuarioService` solicita guardar los cambios.
9. `UsuarioDAO` actualiza los datos.
10. `UsuarioDAO` confirma la actualización.
11. `AppStudyMatch` muestra el mensaje de éxito.
12. El usuario visualiza el perfil actualizado.

### Resultado

La información del perfil académico se mantiene actualizada.

---

## 11.5 Secuencia de administración de usuarios y roles

La secuencia se deriva de HU4 y del caso de uso “Administrar usuarios y roles”.

### Participantes

- Administrador.
- Módulo de administración.
- UsuarioService.
- UsuarioDAO.

### Flujo

1. El administrador ingresa al módulo de administración.
2. El módulo solicita la información de los usuarios.
3. `UsuarioService` solicita los usuarios registrados.
4. `UsuarioDAO` devuelve la información.
5. El módulo muestra los usuarios.
6. El administrador selecciona un usuario.
7. El administrador solicita alternar el rol.
8. `UsuarioService` procesa el cambio.
9. `UsuarioDAO` actualiza el rol.
10. El sistema confirma la modificación.

### Resultado

El usuario seleccionado queda asociado al rol definido por el administrador.

---

# 12. Diseño de clases

## 12.1 TipoRol

Enumeración que define los roles permitidos:

```text
ADMINISTRADOR
PROFESOR
ORGANIZADOR
ESTUDIANTE
```

## 12.2 Usuario

### Atributos

- `idUsuario: int`
- `nombres: String`
- `apellidos: String`
- `correo: String`
- `contraseña: String`
- `carrera: String`
- `estado: Boolean`
- `rol: TipoRol`

### Operaciones

- `actualizarPerfil()`
- `cambiarEstado()`

### Responsabilidad

Representar a una persona registrada en StudyMatch y mantener sus datos, estado y rol.

## 12.3 Horario

### Atributos

- `idHorario: int`
- `dia: String`
- `horaInicio: Time`
- `horaFin: Time`

### Operación

- `actualizarHorario()`

### Relación

Un usuario puede tener cero o varios horarios.

### Consideración

La entidad se conserva como parte del modelo general. La gestión completa de horarios no constituye una historia independiente del Sprint 1.

## 12.4 Participación

### Atributos

- `idParticipacion: int`
- `fechaIngreso: Date`
- `estado: String`

### Operaciones

- `activar()`
- `retirar()`

### Relación

Relaciona usuarios y círculos de estudio.

## 12.5 CírculoEstudio

### Atributos

- `idCirculo: int`
- `nombre: String`
- `descripcion: String`
- `estado: String`
- `fechaCreacion: Date`

### Operaciones

- `actualizarInformacion()`
- `cambiarEstado()`

## 12.6 SesionEstudio

### Atributos

- `idSesion: int`
- `fecha: Date`
- `horaInicio: Time`
- `horaFin: Time`
- `tema: String`
- `estado: String`

### Operaciones

- `reprogramar()`
- `cancelar()`

## 12.7 Asistencia

### Atributos

- `idAsistencia: int`
- `fechaRegistro: DateTime`
- `estado: String`

### Operación

- `registrar()`

## 12.8 Reserva

### Atributos

- `idReserva: int`
- `aula: String`
- `fecha: Date`
- `horaInicio: Time`
- `horaFin: Time`
- `estado: String`

### Operaciones

- `confirmar()`
- `cancelar()`

---

# 13. Relaciones principales del modelo

| Relación | Cardinalidad |
| --- | --- |
| Usuario — Horario | 1 a 0..* |
| Usuario — Participación | 1 a 0..* |
| Usuario — Asistencia | 1 a 0..* |
| Participación — CírculoEstudio | 0..* a 1 |
| CírculoEstudio — SesionEstudio | 1 a 0..* |
| SesionEstudio — Asistencia | 1 a 0..* |
| SesionEstudio — Reserva | 1 a 0..1 |

Las relaciones se conservan de acuerdo con los diagramas de clases y de datos proporcionados.

---

# 14. Diseño de base de datos

## 14.1 Motor

MySQL 8.x.

## 14.2 Tablas identificadas en el estado actual del proyecto

- `usuarios`
- `horario_disponibilidad`
- `circulo_estudio`
- `miembrecia_circulo`

El nombre `miembrecia_circulo` se mantiene como parte del estado actual del proyecto. Cualquier modificación de nombre deberá considerar el código que dependa de dicha tabla.

## 14.3 Entidades representadas en el modelo de datos

El modelo UML de datos también representa:

- Usuario.
- Horario.
- Participación.
- CírculoEstudio.
- SesionEstudio.
- Asistencia.
- Reserva.

## 14.4 Tabla Usuario

### Campos representados

- `idUsuario`
- `nombres`
- `apellidos`
- `correo`
- `contrasena`
- `carrera`
- `estado`
- `rol`

### Reglas

- `idUsuario` es la clave primaria.
- `correo` debe ser único.
- `rol` debe corresponder a uno de los valores definidos.
- `estado` representa la condición del usuario.
- La contraseña no debe almacenarse en texto plano y debe cumplir la exigencia de protección indicada en la Definition of Done.

## 14.5 Tabla Horario

### Campos representados

- `idHorario`
- `dia`
- `horaInicio`
- `horaFin`

### Relación

Cada horario pertenece a un usuario.

## 14.6 Persistencia del Sprint 1

Las operaciones mínimas relacionadas con el Sprint 1 son:

- insertar usuario;
- consultar usuario;
- actualizar usuario;
- actualizar perfil;
- actualizar estado;
- actualizar rol;
- consultar horarios asociados cuando el perfil lo requiera.

---

# 15. Diseño de despliegue

## 15.1 PC cliente

### Entorno

Navegador web.

### Artefacto

`studymatch-frontend`

### Comunicación

El cliente se comunica con el servidor de aplicaciones mediante HTTP.

## 15.2 Servidor de aplicaciones

### Entorno

JDK 21.

### Artefacto

`studymatch-backend`

### Puerto representado

`8080`

### Responsabilidad

Ejecutar la lógica del sistema y atender las solicitudes del frontend.

## 15.3 Servidor de base de datos

### Entorno

MySQL 8.

### Base de datos

`studymatch-database`

### Puerto representado

`3306`

### Comunicación

El backend se conecta mediante JDBC.

## 15.4 Flujo de despliegue

```text
PC Cliente
   |
   | HTTP 8080
   v
Servidor de aplicaciones
   |
   | JDBC 3306
   v
Servidor de base de datos
```

---

# 16. Requisitos funcionales por historia

## 16.1 HU1 — Registrar estudiante

### Entradas

- Nombres.
- Apellidos.
- Correo institucional.
- Contraseña.
- Carrera.
- Datos académicos disponibles en el formulario.

### Proceso

1. Mostrar el formulario.
2. Recibir los datos.
3. Validar campos obligatorios.
4. Validar formato del correo.
5. Verificar que el usuario no esté duplicado.
6. Registrar al usuario.
7. Mostrar confirmación.

### Salida

Cuenta registrada o mensaje de validación.

---

## 16.2 HU2 — Iniciar sesión

### Entradas

- Correo.
- Contraseña.

### Proceso

1. Recibir credenciales.
2. Consultar al usuario.
3. Validar las credenciales.
4. Verificar el estado y rol.
5. Permitir o rechazar el acceso.
6. Mostrar el resultado.

### Salida

Acceso autorizado según rol o mensaje de error.

---

## 16.3 HU3 — Gestionar perfil académico

### Entradas

Datos actualizados del usuario.

### Proceso

1. Consultar el perfil.
2. Mostrar la información.
3. Permitir la edición.
4. Validar los datos.
5. Actualizar el perfil.
6. Guardar los cambios.
7. Mostrar confirmación.

### Salida

Perfil actualizado.

---

## 16.4 HU4 — Administrar usuarios y roles

### Entradas

- Usuario seleccionado.
- Rol definido por el administrador.
- Estado, cuando corresponda al proceso de validación.

### Proceso

1. Mostrar usuarios.
2. Seleccionar usuario.
3. Consultar la información.
4. Modificar el rol.
5. Actualizar la información.
6. Mostrar confirmación.

### Salida

Usuario actualizado con el rol correspondiente.

---

# 17. Reglas y validaciones

## 17.1 Registro

- Los campos obligatorios no deben estar vacíos.
- El correo debe tener un formato válido.
- El correo debe corresponder al criterio institucional definido por el sistema.
- No se deben registrar correos duplicados.
- No se deben guardar registros incompletos.
- La contraseña debe almacenarse de forma protegida.

## 17.2 Inicio de sesión

- El usuario debe existir.
- Las credenciales deben ser válidas.
- El acceso debe responder al rol asociado.
- Un usuario no debe acceder a funcionalidades que no correspondan a su rol.

## 17.3 Perfil académico

- El usuario debe estar autenticado.
- Los datos obligatorios deben mantenerse completos.
- Los formatos inválidos deben ser rechazados.
- Los cambios deben quedar almacenados en la base de datos.

## 17.4 Administración

- Solo el administrador puede acceder a la gestión de usuarios y roles.
- El usuario seleccionado debe existir.
- El rol asignado debe pertenecer a `TipoRol`.
- La actualización debe reflejarse en el sistema.

## 17.5 Horarios, sesiones y reservas

La Definition of Done establece que las funcionalidades relacionadas con horarios, sesiones o reservas deben validar cruces y mostrar una alerta antes de confirmar.

Para el Sprint 1, esta condición se aplicará únicamente cuando una funcionalidad implementada dentro del alcance gestione efectivamente horarios. Las sesiones y reservas pertenecen al modelo general y no forman parte de las cuatro historias aprobadas.

---

# 18. Criterios de aceptación

## 18.1 HU1 — Registrar estudiante

1. Dado un estudiante que completa los campos requeridos, cuando envía el formulario, entonces el sistema registra la cuenta.
2. Dado un formulario con campos obligatorios vacíos, cuando se intenta registrar, entonces el sistema muestra las validaciones.
3. Dado un correo con formato inválido, cuando se intenta registrar, entonces el sistema rechaza el registro.
4. Dado un correo ya registrado, cuando se intenta crear una nueva cuenta, entonces el sistema evita el duplicado.
5. Dado un registro exitoso, cuando se consulta la base de datos, entonces la información se encuentra almacenada.
6. Dado un registro exitoso, entonces la contraseña no queda almacenada en texto plano.

## 18.2 HU2 — Iniciar sesión

1. Dado un usuario registrado con credenciales correctas, cuando inicia sesión, entonces el sistema permite el acceso.
2. Dado un usuario con credenciales incorrectas, cuando intenta iniciar sesión, entonces el sistema muestra un mensaje de error.
3. Dado un usuario autenticado, cuando accede al sistema, entonces visualiza las funcionalidades correspondientes a su rol.
4. Dado un usuario sin permisos, cuando intenta acceder a una función restringida, entonces el sistema rechaza el acceso.

## 18.3 HU3 — Gestionar perfil académico

1. Dado un usuario autenticado, cuando abre el perfil, entonces el sistema muestra su información.
2. Dado un usuario que modifica datos válidos, cuando guarda los cambios, entonces el sistema actualiza el perfil.
3. Dado un usuario que deja campos obligatorios vacíos, cuando intenta guardar, entonces el sistema evita la actualización.
4. Dado un usuario que ingresa formatos inválidos, cuando intenta guardar, entonces el sistema muestra las validaciones.
5. Dado un cambio exitoso, cuando se vuelve a consultar el perfil, entonces se muestran los datos actualizados.

## 18.4 HU4 — Administrar usuarios y roles

1. Dado un administrador autenticado, cuando ingresa al módulo, entonces visualiza los usuarios registrados.
2. Dado un administrador que selecciona un usuario, cuando modifica su rol, entonces el sistema guarda el cambio.
3. Dado un rol no válido, cuando se intenta asignar, entonces el sistema rechaza la modificación.
4. Dado un usuario sin rol de administrador, cuando intenta acceder al módulo, entonces el sistema restringe el acceso.
5. Dado un cambio exitoso, cuando se consulta nuevamente al usuario, entonces se muestra el rol actualizado.

---

# 19. Tareas de implementación

## 19.1 Tareas generales

- Verificar la estructura del repositorio.
- Configurar la conexión entre frontend y backend.
- Configurar la conexión JDBC con MySQL.
- Verificar la ejecución del script de base de datos.
- Mantener la separación por capas.
- Centralizar las direcciones de los servicios del frontend.
- Documentar pasos manuales de configuración.

## 19.2 Tareas HU1

- Crear o revisar la pantalla de registro.
- Crear el DTO de registro.
- Implementar el procesamiento del registro.
- Implementar la validación de campos obligatorios.
- Implementar la validación del correo institucional.
- Implementar la validación de duplicados.
- Implementar el registro en `usuarios`.
- Mostrar mensajes de éxito o error.
- Verificar el almacenamiento protegido de la contraseña.

## 19.3 Tareas HU2

- Crear o revisar la pantalla de inicio de sesión.
- Crear el DTO de credenciales.
- Implementar la consulta del usuario.
- Implementar la validación de credenciales.
- Determinar el acceso según el rol.
- Mostrar mensajes de resultado.
- Verificar la restricción de acceso.

## 19.4 Tareas HU3

- Crear o revisar la pantalla de perfil.
- Implementar la consulta del perfil.
- Mostrar datos del usuario.
- Permitir la edición de los campos definidos.
- Validar los datos actualizados.
- Implementar la actualización en la base de datos.
- Mostrar confirmación.
- Verificar la persistencia de los cambios.

## 19.5 Tareas HU4

- Crear o revisar el módulo de administración.
- Implementar la consulta de usuarios.
- Mostrar la lista de usuarios.
- Permitir seleccionar un usuario.
- Implementar el cambio de rol.
- Validar el rol asignado.
- Actualizar la información.
- Restringir el módulo a administradores.
- Mostrar confirmación.

## 19.6 Tareas del flujo complementario de recuperación

- Mantener la pantalla o formulario representado.
- Consultar la existencia del usuario.
- Procesar el envío del enlace o código representado.
- Mostrar el resultado al usuario.

Estas tareas no representan una nueva historia de usuario.

---

# 20. Plan de verificación

## 20.1 Preparación

1. Crear la base de datos mediante `schema.sql`.
2. Verificar MySQL 8.x.
3. Configurar la conexión del backend.
4. Ejecutar el backend con Java 21.
5. Verificar el servicio en el puerto 8080.
6. Ejecutar el frontend con Vite.
7. Abrir la aplicación en un navegador moderno.

## 20.2 Verificación de HU1

- Registrar un usuario con datos válidos.
- Intentar registrar campos vacíos.
- Intentar registrar un correo inválido.
- Intentar registrar un correo duplicado.
- Consultar la tabla `usuarios`.
- Verificar que la contraseña no esté guardada en texto plano.

## 20.3 Verificación de HU2

- Iniciar sesión con credenciales válidas.
- Iniciar sesión con correo incorrecto.
- Iniciar sesión con contraseña incorrecta.
- Verificar el acceso según rol.
- Verificar el rechazo de funcionalidades no permitidas.

## 20.4 Verificación de HU3

- Abrir el perfil.
- Verificar los datos mostrados.
- Modificar un dato válido.
- Guardar el cambio.
- Volver a consultar el perfil.
- Intentar guardar datos inválidos.

## 20.5 Verificación de HU4

- Ingresar como administrador.
- Consultar usuarios.
- Seleccionar un usuario.
- Cambiar el rol.
- Verificar la actualización.
- Intentar acceder con un usuario no administrador.

## 20.6 Evidencia

La evidencia puede incluir:

- capturas de las pantallas;
- resultados de validación;
- registros almacenados en MySQL;
- mensajes devueltos por el backend;
- pruebas de acceso según rol;
- lista de casos ejecutados;
- resultado esperado y resultado obtenido.

---

# 21. Matriz de trazabilidad

| Historia | Caso de uso | Secuencia | Clase principal | Persistencia | Interfaz |
| --- | --- | --- | --- | --- | --- |
| HU1 | Registrar usuario / Completar registro | Registro | Usuario | usuarios | FrmRegistro |
| HU2 | Iniciar sesión / Ingresar | Inicio de sesión | Usuario | usuarios | AppStudyMatch |
| HU3 | Gestionar perfil / Editar datos / Actualizar perfil | Perfil académico | Usuario, Horario | usuarios, horario_disponibilidad | Perfil |
| HU4 | Administrar usuarios y roles / Seleccionar usuario / Alternar rol | Administración | Usuario, TipoRol | usuarios | Panel de administración |

---

# 22. Definition of Done

Una funcionalidad del Sprint 1 se considera terminada cuando cumple las siguientes condiciones:

1. El sistema se encuentra desarrollado utilizando la arquitectura monolítica establecida para StudyMatch.
2. Se mantiene la separación entre frontend, backend y base de datos.
3. La funcionalidad responde correctamente al rol correspondiente:
   - estudiante;
   - organizador;
   - docente;
   - administrador.
4. Se han implementado validaciones para evitar:
   - campos obligatorios vacíos;
   - formatos inválidos;
   - registros duplicados;
   - datos inconsistentes;
   - incumplimiento de reglas de negocio.
5. Las validaciones han sido verificadas mediante pruebas y existe evidencia de los resultados.
6. Las funcionalidades que gestionan horarios, sesiones o reservas validan cruces cuando corresponda a su alcance.
7. Las funcionalidades que gestionan información del usuario se encuentran protegidas mediante autenticación.
8. Las contraseñas se almacenan de forma protegida y no en texto plano.
9. Los componentes desarrollados en React son consistentes con los mockups aprobados.
10. La interfaz se visualiza correctamente en distintos tipos de pantalla.
11. La aplicación funciona en navegadores modernos.
12. La implementación respeta la separación de responsabilidades definida en este SDD.
13. La funcionalidad puede ejecutarse y verificarse localmente.
14. Los pasos manuales necesarios se encuentran documentados.
15. La funcionalidad no rompe el funcionamiento de las capas existentes.

---

# 23. Estado actual del proyecto

| Capa | Estado actual |
| --- | --- |
| Frontend | Base implementada con React, Vite, componentes y pantallas principales |
| Backend | Base implementada con Java 21, servidor HTTP nativo y arquitectura por capas |
| Base de datos | Script SQL disponible |
| Documentación | README y constitución SDD/OpenSpec disponibles |
| Requisitos específicos | Definidos en este SDD |
| Tareas | Organizadas por historia de usuario |
| Verificación | Definida por funcionalidad |
| Evidencia | Pendiente de completar durante la implementación y pruebas |

---

# 24. Riesgos y limitaciones

## 24.1 Diferencia entre modelo general y alcance del sprint

Los diagramas incluyen entidades y procesos pertenecientes al sistema completo. El equipo debe evitar implementar funcionalidades de círculos, sesiones, asistencias o reservas como parte del Sprint 1, salvo que sean necesarias para mantener la estructura ya existente.

## 24.2 Flujo de recuperación

El restablecimiento de contraseña aparece en los diagramas, pero no está definido como historia de usuario independiente. Debe mantenerse como flujo complementario sin modificar el backlog oficial.

## 24.3 Dependencia entre capas

El frontend depende de que el backend exponga correctamente sus servicios. El backend depende de la correcta configuración de MySQL y JDBC.

## 24.4 Configuración manual

La ejecución local puede requerir configuración de:

- credenciales de base de datos;
- nombre de la base;
- puertos;
- dirección del backend;
- datos iniciales;
- usuario administrador.

## 24.5 Consistencia documental

Los diagramas, el código y este SDD deben mantenerse alineados. Si durante la implementación se modifica una clase, tabla o relación, se deberá revisar su impacto documental sin ampliar las historias de usuario aprobadas.

---

# 25. Decisiones técnicas registradas

| Decisión | Justificación |
| --- | --- |
| Utilizar Java 21 | Tecnología definida para el backend |
| Utilizar servidor HTTP nativo | Restricción técnica y estructura actual del proyecto |
| Utilizar React con Vite | Tecnología definida para el frontend |
| Utilizar Tailwind CSS | Herramienta utilizada para la interfaz |
| Utilizar MySQL 8.x | Motor de base de datos definido |
| Separar controller, service, repository, dto y entity | Mantener organización y responsabilidades |
| Mantener frontend, backend y base de datos separados | Cumplir la arquitectura establecida |
| Documentar solo HU1, HU2, HU3 y HU4 | Mantener fidelidad con el Sprint 1 aprobado |
| Conservar entidades generales como contexto | Mantener coherencia con los diagramas UML |

---

# 26. Resultado esperado del Sprint 1

Al finalizar el Sprint 1, StudyMatch debe disponer de una base funcional para:

- registrar estudiantes;
- autenticar usuarios;
- permitir el acceso según rol;
- consultar y actualizar el perfil académico;
- administrar usuarios y roles;
- conservar la información en MySQL;
- operar mediante una interfaz React conectada a un backend Java;
- mantener la trazabilidad entre historias, diagramas, implementación y pruebas.

Este resultado establece la base para las funcionalidades de círculos de estudio, horarios, sesiones, asistencias y reservas que forman parte del modelo general del sistema.
