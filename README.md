# 🎓 StudyMatch - Arquitectura Base (Sprint 1)

Este proyecto está dividido en tres capas totalmente desacopladas que se ejecutan por separado: **Frontend**, **Backend** y **Base de Datos**. Sigue estas instrucciones para levantar cada entorno de manera independiente en tu máquina local.

---

## 🛠️ Tecnologías Obligatorias

Asegúrate de contar con las siguientes versiones exactas para evitar conflictos:

* **Java Development Kit (JDK):** Versión 21 ☕
* **Node.js:** Versión 18 o superior 🟢
* **Motor de Base de Datos:** MySQL Server 8.x 🐬

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
2. Abre el archivo de configuración de base de datos (`DatabaseConfig.java` o el correspondiente).
3. **Ajuste Obligatorio:** Reemplaza las credenciales de conexión con tu usuario y contraseña local de MySQL:

   ```java
   // Verifica que coincida con tu entorno local
   String url = "jdbc:mysql://localhost:3306/studymatch";
   String user = "tu_usuario_mysql";
   String password = "tu_contrasena_mysql";
4. Ejecuta la clase principal (`Main.java` o la clase que contiene tu método `public static void main` y levanta el servidor).

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
