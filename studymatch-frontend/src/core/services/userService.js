// src/core/services/userService.js

import { ENV } from "../../config/environment";

/**
 * Servicio de gestion general de usuarios de StudyMatch.
 * No maneja estado de React: unicamente ejecuta peticiones de red
 * con fetch() y retorna promesas, o lanza errores descriptivos.
 */

/**
 * Recupera la lista completa de usuarios registrados en StudyMatch.
 *
 * @returns {Promise<Array<Object>>} La lista de usuarios (sin contrasena)
 *  mapeada por el backend.
 * @throws {Error} Si ocurre un error de red o el backend responde con un estado no exitoso.
 */
export async function getAllUsers() {
  let response;

  try {
    response = await fetch(`${ENV.API_URL}/usuarios`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });
  } catch (errorDeRed) {
    throw new Error(
      "No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente."
    );
  }

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.mensaje || "No se pudo obtener la lista de usuarios.");
  }

  return response.json();
}
