# 🎓 StudyMatch - Arquitectura Base (Sprint 1)

Este proyecto está dividido en tres capas totalmente desacopladas: **Frontend**, **Backend** y **Base de Datos**. Podés levantar todo el entorno con Docker Compose o ejecutar cada capa de manera independiente en tu máquina local.

---

## 🐳 Ejecución rápida con Docker Compose

Desde la raíz del repositorio:

```bash
docker compose up --build
```

Servicios y URLs:

- Frontend: <http://localhost:5173>
- Backend API: <http://localhost:8080/api>
- MySQL: disponible en el host como `localhost:3307` para evitar conflictos con un MySQL local en `3306`.

El contenedor `mysql` crea la base `studymatch_db`, el usuario `studymatch_user` con contraseña `123456`, y carga `studymatch-database/schema.sql` automáticamente desde `/docker-entrypoint-initdb.d/` cuando el volumen se inicializa por primera vez.

Comandos útiles:

```bash
# Detener contenedores sin borrar datos
docker compose down

# Detener y resetear la base de datos Docker
docker compose down -v
```

Si querés cambiar la URL de API del frontend o el origen permitido por CORS, ajustá `VITE_API_URL` y `FRONTEND_ORIGIN` en `docker-compose.yml`.

---

## 🛠️ Tecnologías Obligatorias

Asegúrate de contar con las siguientes versiones exactas para evitar conflictos:

- **Java Development Kit (JDK):** Versión 21 ☕
- **Node.js:** Versión 18 o superior 🟢
- **Motor de Base de Datos:** MySQL Server 8.x 🐬

---

## 🚀 Guía de Despliegue por Capas

Para probar la aplicación completa, debes levantar los tres componentes de forma consecutiva siguiendo este orden estricto:

### 1️⃣ Capa de Datos: MySQL Server

La persistencia de datos corre a nivel local.

1. Abre tu gestor de base de datos preferido (MySQL Workbench, DBeaver, phpMyAdmin).
2. Conéctate a tu instancia local de **MySQL Server**.
3. Ejecuta el script de creación de tablas (`schema.sql` o similar que se encuentra en el proyecto) para estructurar la tabla `usuarios`.

> ⚠️ **Pruebas del Módulo Admin:** Actualmente **no existe un usuario maestro inyectado de fábrica**. Para probar el panel de administración, debes:
>
> 1. Registrarte normalmente como un usuario nuevo desde el formulario de la aplicación.
> 2. Ir a tu base de datos y ejecutar un `UPDATE usuarios SET rol = 'Administrador' WHERE correo_institucional = 'tu_correo';` para elevar tus privilegios manualmente.

---

### 2️⃣ Capa de Backend: Java 21

El servidor es una aplicación standalone basada en el `HttpServer` nativo de Java.

1. Abre la carpeta del backend en tu IDE (VS Code, IntelliJ o Eclipse).
2. Verificá que tu MySQL local tenga la base `studymatch_db`, el usuario `studymatch_user` y la contraseña `123456`, o definí variables de entorno antes de iniciar el backend:

   ```bash
   export DB_HOST=localhost
   export DB_PORT=3306
   export DB_NAME=studymatch_db
   export DB_USER=studymatch_user
   export DB_PASSWORD=123456
   # Opcional: DB_URL puede reemplazar host/puerto/nombre.
   ```

3. Ejecuta la clase principal (`App.java`) o el jar generado con Maven.

> 💡 **Nota:** El backend quedará escuchando peticiones de la API en el puerto: `http://localhost:8080`

---

### 3️⃣ Capa de Frontend: React + Vite + Tailwind CSS

El cliente visual corre en un servidor de Node independiente.

1. Abre una terminal nueva apuntando directamente a la raíz de la carpeta del Frontend (ej. `cd Frontend`).
2. Descarga los módulos de Node e inicializa el servidor de desarrollo ejecutando:

   ```bash
   # Instalar dependencias esenciales (Lucide-React, React Router, Tailwind, etc.)
   npm install

   # Encender el servidor de desarrollo de Vite
   npm run dev
   ```

---

## 📋 SDD / OpenSpec

Durante el Sprint 1 se incorporó **SDD / OpenSpec** como apoyo metodológico para documentar reglas técnicas, decisiones pendientes, criterios de aceptación, tareas y verificación del trabajo restante.

> **Nota de transparencia:** SDD no se presenta como una metodología aplicada retroactivamente a todo el sprint. Se incorporó durante el sprint aún en desarrollo para controlar y documentar el trabajo pendiente de forma trazable.
