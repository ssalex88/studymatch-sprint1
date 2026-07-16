// src/core/services/authService.js

import { ENV } from "../../config/environment";

/**
 * Servicio de autenticacion de StudyMatch.
 * No maneja estado de React (useState, etc.): unicamente ejecuta las
 * peticiones de red con fetch() y retorna promesas, o lanza errores
 * con el mensaje correspondiente para que la capa de UI los capture.
 */

/**
 * Registra un nuevo usuario en StudyMatch.
 *
 * @param {Object} userData - Datos del formulario de registro
 *  (nombreCompleto, correoInstitucional, contrasena, carrera, ciclo).
 * @returns {Promise<Object>} El JSON del usuario creado (sin contrasena); la sesion queda en cookie HttpOnly.
 * @throws {Error} Con el mensaje exacto enviado por el backend si la respuesta no es exitosa.
 */
export async function registerUser(userData) {
	const response = await fetch(`${ENV.API_URL}/auth/registrar`, {
		method: "POST",
		credentials: "include",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(userData),
	});

	if (!response.ok) {
		const errorData = await response.json().catch(() => ({}));
		throw new Error(errorData.mensaje || "No se pudo completar el registro.");
	}

	return response.json();
}

/**
 * Inicia sesion con las credenciales del usuario en StudyMatch.
 *
 * @param {Object} credentials - Credenciales de acceso
 *  (correoInstitucional, contrasena).
 * @returns {Promise<Object>} El JSON con los datos seguros del usuario autenticado; la sesion queda en cookie HttpOnly.
 * @throws {Error} Con el mensaje exacto enviado por el backend si las credenciales
 *  son incorrectas (401) o la respuesta no es exitosa.
 */
export async function loginUser(credentials) {
	const response = await fetch(`${ENV.API_URL}/auth/login`, {
		method: "POST",
		credentials: "include",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(credentials),
	});

	if (!response.ok) {
		const errorData = await response.json().catch(() => ({}));
		throw new Error(errorData.mensaje || "Credenciales de acceso incorrectas.");
	}

	return response.json();
}

/**
 * Cierra la sesion backend actual e invalida la cookie HttpOnly.
 *
 * @returns {Promise<Object>} Respuesta JSON del backend.
 * @throws {Error} Si el backend rechaza el cierre de sesion.
 */
export async function logoutUser() {
	const response = await fetch(`${ENV.API_URL}/auth/logout`, {
		method: "POST",
		credentials: "include",
	});

	if (!response.ok) {
		const errorData = await response.json().catch(() => ({}));
		throw new Error(errorData.mensaje || "No se pudo cerrar la sesion.");
	}

	return response.json();
}
