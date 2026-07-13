# Constitución SDD de StudyMatch

## 1. Propósito

Esta constitución define las reglas técnicas, restricciones y criterios de validación que guían el trabajo restante del **Sprint 1 de StudyMatch** mediante un proceso orientado a **SDD / OpenSpec**.

SDD se incorporó durante el sprint como mecanismo de trazabilidad y documentación. No se presenta como una metodología aplicada retroactivamente desde el inicio del proyecto.

## 2. Alcance del proyecto

StudyMatch está estructurado como una aplicación desacoplada con tres capas principales:

- **Frontend:** aplicación React para la interacción con el usuario.
- **Backend:** aplicación Java nativa que expone endpoints HTTP.
- **Base de datos:** base de datos relacional MySQL para persistencia.

Cada capa debe mantenerse comprensible, ejecutable y mantenible de forma independiente.

## 3. Restricciones técnicas principales

El proyecto debe respetar las siguientes restricciones:

- El backend debe usar **Java 21**.
- El backend debe usar capacidades nativas de Java y evitar frameworks de backend.
- El servidor HTTP del backend debe basarse en `HttpServer` nativo de Java o APIs nativas equivalentes.
- El motor de base de datos debe ser **MySQL 8.x**.
- El frontend debe usar **React** con la estructura existente basada en Vite.
- Las responsabilidades de frontend, backend y base de datos deben permanecer separadas.

## 4. Reglas de arquitectura del backend

El backend debe seguir una arquitectura por capas. Cada capa tiene una responsabilidad específica:

- **Entity:** representa los datos persistentes del dominio y estructuras asociadas a la base de datos.
- **DTO:** transporta datos entre los límites de la API y la lógica interna sin exponer detalles innecesarios de persistencia.
- **Repository:** encapsula el acceso a la base de datos y las operaciones SQL.
- **Service:** contiene la lógica de negocio, validaciones y coordinación entre repositorios y controladores.
- **Controller:** maneja solicitudes HTTP, interpreta entradas, llama a servicios y devuelve respuestas HTTP.

Reglas:

- Los controladores no deben contener lógica SQL directa.
- Los repositorios no deben contener lógica de respuesta HTTP.
- Los servicios no deben depender de conceptos específicos del frontend.
- Los DTO deben usarse al exponer o recibir datos estructurados mediante la API.
- Las credenciales de base de datos no deben quedar hardcodeadas para uso productivo; los valores de configuración local pueden documentarse por separado.

## 5. Reglas de arquitectura del frontend

El frontend debe conservar una organización clara por componentes y funcionalidades.

Reglas:

- Los componentes de UI deben mantenerse reutilizables cuando sea posible.
- La lógica específica de una funcionalidad debe ubicarse cerca de su carpeta correspondiente.
- La configuración de endpoints de API debe estar centralizada y no duplicada en distintos componentes.
- Los flujos de usuario deben manejar estados de éxito, carga y error cuando corresponda.
- El código frontend no debe asumir directamente la estructura de la base de datos; debe comunicarse mediante contratos de API del backend.

## 6. Reglas de base de datos

La capa de base de datos debe preservar la integridad relacional y una nomenclatura clara.

Reglas:

- Las tablas y columnas deben usar nombres consistentes.
- Las claves primarias y foráneas deben ser explícitas cuando existan relaciones.
- Los scripts SQL deben mantenerse reproducibles para la configuración local.
- El comportamiento asociado a roles, como el acceso de administrador, debe documentarse cuando requiera configuración manual.

## 7. Reglas de criterios de aceptación

Toda funcionalidad pendiente completada después de adoptar esta constitución debe definir criterios de aceptación antes de considerarse finalizada.

Una funcionalidad es aceptable solo si:

- El comportamiento esperado para el usuario está claro.
- Se identifican las pantallas del frontend o endpoints del backend necesarios.
- Se consideran casos de éxito y de error.
- Los datos requeridos por la funcionalidad existen o están documentados.
- La implementación puede verificarse manualmente.

## 8. Definición de terminado

Una tarea se considera terminada cuando:

- La implementación satisface los criterios de aceptación relacionados.
- La capa afectada respeta las reglas de arquitectura definidas anteriormente.
- El proyecto puede seguir ejecutándose localmente según las instrucciones del README.
- Cualquier configuración de base de datos o paso manual requerido queda documentado.
- El equipo revisó el resultado frente al objetivo del sprint.

## 9. Transparencia sobre uso de IA

Las herramientas de IA pueden utilizarse como apoyo para planificación, documentación, revisión y aclaración técnica.

Reglas:

- Las sugerencias generadas por IA deben ser revisadas por el equipo antes de considerarse decisiones del proyecto.
- Las respuestas de IA no deben copiarse directamente al proyecto sin validación.
- El informe académico puede documentar la asistencia de IA por separado como evidencia.
- Los artefactos del repositorio deben enfocarse en trazabilidad técnica, metodología y reglas del proyecto.

## 10. Trazabilidad SDD del Sprint 1

Para el trabajo restante del Sprint 1, los artefactos SDD / OpenSpec deben ayudar a conectar:

- reglas del proyecto,
- requisitos pendientes,
- decisiones de diseño técnico,
- tareas de implementación,
- y evidencia de verificación.

Esta constitución actúa como acuerdo base para esa trazabilidad.
