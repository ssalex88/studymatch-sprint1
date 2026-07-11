// src/config/environment.js

/**
 * Configuracion centralizada del entorno de StudyMatch.
 * Este es el UNICO punto del sistema donde se debe definir el host
 * y puerto del backend. Ningun servicio ni componente debe hardcodear
 * la URL de la API; todos deben importar ENV.API_URL desde aqui.
 */
export const ENV = {
  API_URL: "http://localhost:8080/api",
};
