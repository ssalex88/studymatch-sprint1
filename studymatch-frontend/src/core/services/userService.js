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
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		const errorData = await response.json().catch(() => ({}));
		throw new Error(
			errorData.mensaje || "No se pudo obtener la lista de usuarios.",
		);
	}

	return response.json();
}

/**
 * Recupera un usuario por identificador desde el backend.
 *
 * @param {number|string} idUsuario - Identificador del usuario a consultar.
 * @returns {Promise<Object>} Datos seguros del usuario (sin contrasena).
 * @throws {Error} Si ocurre un error de red o el backend responde con error.
 */
export async function getUserById(idUsuario) {
	let response;

	try {
		response = await fetch(`${ENV.API_URL}/usuarios/${idUsuario}`, {
			method: "GET",
			headers: {
				"Content-Type": "application/json",
			},
		});
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		const errorData = await response.json().catch(() => ({}));
		throw new Error(
			errorData.mensaje || "No se pudo obtener la informacion del usuario.",
		);
	}

	return response.json();
}

/**
 * Actualiza el rol asignado a un usuario usando el endpoint existente de HU4.
 *
 * @param {number|string} idUsuario - Identificador del usuario a modificar.
 * @param {string} rol - Nuevo rol a asignar.
 * @returns {Promise<Object>} Respuesta JSON del backend.
 * @throws {Error} Si ocurre un error de red o el backend rechaza la actualizacion.
 */
export async function updateUserRole(idUsuario, rol) {
	let response;

	try {
		response = await fetch(`${ENV.API_URL}/usuarios/${idUsuario}/rol`, {
			method: "PATCH",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify({ rol }),
		});
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		const errorData = await response.json().catch(() => ({}));
		throw new Error(
			errorData.mensaje || "No se pudo actualizar el rol del usuario.",
		);
	}

	return response.json();
}
