// src/core/services/userService.js

import { ENV } from "../../config/environment";

/**
 * Servicio de gestion general de usuarios de StudyMatch.
 * No maneja estado de React: unicamente ejecuta peticiones de red
 * con fetch() y retorna promesas, o lanza errores descriptivos.
 */

function getSessionToken() {
	const tokenDirecto = localStorage.getItem("sessionToken");
	if (tokenDirecto) {
		return tokenDirecto;
	}

	const datosGuardados = localStorage.getItem("currentUser");
	if (!datosGuardados) {
		return "";
	}

	try {
		const usuario = JSON.parse(datosGuardados);
		return usuario.sessionToken || "";
	} catch {
		return "";
	}
}

function buildJsonHeaders() {
	const headers = {
		"Content-Type": "application/json",
	};
	const sessionToken = getSessionToken();

	if (sessionToken) {
		headers.Authorization = `Bearer ${sessionToken}`;
	}

	return headers;
}

async function parseBackendError(response, fallbackMessage) {
	const errorData = await response.json().catch(() => ({}));
	return new Error(errorData.mensaje || fallbackMessage);
}

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
			headers: buildJsonHeaders(),
		});
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		throw await parseBackendError(
			response,
			"No se pudo obtener la lista de usuarios.",
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
			headers: buildJsonHeaders(),
		});
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		throw await parseBackendError(
			response,
			"No se pudo obtener la informacion del usuario.",
		);
	}

	return response.json();
}

/**
 * Actualiza los datos academicos del perfil de un usuario.
 *
 * @param {number|string} idUsuario - Identificador del usuario a modificar.
 * @param {Object} profileData - Campos editables del perfil academico.
 * @returns {Promise<Object>} Respuesta JSON del backend.
 * @throws {Error} Si ocurre un error de red o el backend rechaza la actualizacion.
 */
export async function updateUserProfile(idUsuario, profileData) {
	let response;

	try {
		response = await fetch(`${ENV.API_URL}/usuarios/${idUsuario}`, {
			method: "PUT",
			headers: buildJsonHeaders(),
			body: JSON.stringify(profileData),
		});
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		throw await parseBackendError(response, "No se pudo actualizar el perfil.");
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
			headers: buildJsonHeaders(),
			body: JSON.stringify({ rol }),
		});
	} catch {
		throw new Error(
			"No se pudo conectar con el servidor. Verifica tu conexion e intenta nuevamente.",
		);
	}

	if (!response.ok) {
		throw await parseBackendError(
			response,
			"No se pudo actualizar el rol del usuario.",
		);
	}

	return response.json();
}
