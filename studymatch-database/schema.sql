-- ============================================================
-- BASE DE DATOS: StudyMatch
-- ARCHIVO: schema.sql (Versión Unificada Completa)
-- ============================================================

-- 1. Eliminamos las tablas en orden inverso por sus claves foráneas para evitar conflictos
DROP TABLE IF EXISTS miembrecia_circulo;
DROP TABLE IF EXISTS circulo_estudio;
DROP TABLE IF EXISTS horario_disponibilidad;
DROP TABLE IF EXISTS usuarios;

-- 2. Creación de la tabla principal de Usuarios (HU3 Actualizada)
CREATE TABLE usuarios (
    id_usuario           INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo      VARCHAR(100)  NOT NULL,
    correo_institucional VARCHAR(150)  NOT NULL UNIQUE,
    codigo_alumno        VARCHAR(20)   UNIQUE, -- Campo nuevo para el perfil
    contrasena           VARCHAR(255)  NOT NULL,
    rol                  VARCHAR(50)   NOT NULL,
    carrera              VARCHAR(100),
    ciclo                VARCHAR(20),
    biografia            TEXT                  -- Campo nuevo para el perfil
);

-- Índice para acelerar búsquedas por correo institucional
CREATE INDEX idx_usuarios_correo ON usuarios (correo_institucional);

-- 3. Creación de la tabla de Horarios Disponibles (HU5)
CREATE TABLE horario_disponibilidad (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    dia_semana VARCHAR(15) NOT NULL, -- 'Lunes', 'Martes', 'Miércoles', etc.
    bloque_hora VARCHAR(30) NOT NULL, -- '08:00 - 10:00', '10:00 - 12:00', etc.
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- 4. Creación de la tabla de Círculos de Estudio (HU6)
CREATE TABLE circulo_estudio (
    id_circulo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_curso VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_creador INT NOT NULL, 
    limite_alumnos INT DEFAULT 6,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_creador) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

-- 5. Creación de la tabla intermedia para los Miembros de los Círculos (HU7)
CREATE TABLE miembrecia_circulo (
    id_circulo INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_union TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_circulo, id_usuario),
    FOREIGN KEY (id_circulo) REFERENCES circulo_estudio(id_circulo) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);