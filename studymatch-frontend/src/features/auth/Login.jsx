// src/features/auth/Login.jsx

import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../../core/services/authService";
import "./Login.css";

/**
 * Pantalla de inicio de sesión de StudyMatch (HU1).
 * Diseño de panel dividido: mitad izquierda verde informativa,
 * mitad derecha blanca con el formulario de acceso.
 */
export default function Login() {
	const navigate = useNavigate();

	const [formData, setFormData] = useState({
		correoInstitucional: "",
		contrasena: "",
	});

	const [error, setError] = useState("");
	const [loading, setLoading] = useState(false);

	const handleChange = (e) => {
		const { name, value } = e.target;
		setFormData((prev) => ({ ...prev, [name]: value }));
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setError("");

		if (!formData.correoInstitucional.trim() || !formData.contrasena.trim()) {
			setError("Por favor ingresa tu correo institucional y tu contraseña.");
			return;
		}

		setLoading(true);
		try {
			const respuesta = await loginUser(formData);
			localStorage.setItem("currentUser", JSON.stringify(respuesta));
			if (respuesta.sessionToken) {
				localStorage.setItem("sessionToken", respuesta.sessionToken);
			}
			navigate("/perfil");
		} catch (err) {
			setError(err.message || "Credenciales de acceso incorrectas.");
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="auth-screen">
			<div className="auth-card">
				<div className="auth-panel auth-panel-green">
					<div className="auth-logo">
						<div className="auth-logo-icon">
							<i className="fas fa-graduation-cap"></i>
						</div>
						<span className="auth-logo-text">studymatch</span>
					</div>

					<div className="auth-panel-content">
						<h1 className="auth-panel-title">Juntos, aprendemos más</h1>
						<p className="auth-panel-subtitle">
							Conecta con compañeros de tu carrera, organiza tu horario de
							estudio y forma parte de círculos académicos hechos a tu medida.
						</p>
					</div>
				</div>

				<div className="auth-panel auth-panel-white">
					<div className="auth-form-wrapper">
						<h2 className="auth-form-title">Iniciar sesión</h2>
						<p className="auth-form-subtitle">
							Ingresa con tu correo institucional y contraseña.
						</p>

						{error && (
							<div className="auth-banner auth-banner-error">
								<i className="fas fa-circle-exclamation"></i>
								<span>{error}</span>
							</div>
						)}

						<form onSubmit={handleSubmit} className="auth-form">
							<div className="auth-input-group">
								<label htmlFor="correoInstitucional">
									Correo institucional
								</label>
								<div className="auth-input-wrapper">
									<i className="fas fa-envelope auth-input-icon"></i>
									<input
										id="correoInstitucional"
										name="correoInstitucional"
										type="email"
										value={formData.correoInstitucional}
										onChange={handleChange}
										placeholder="nombre.apellido@universidad.edu"
									/>
								</div>
							</div>

							<div className="auth-input-group">
								<label htmlFor="contrasena">Contraseña</label>
								<div className="auth-input-wrapper">
									<i className="fas fa-lock auth-input-icon"></i>
									<input
										id="contrasena"
										name="contrasena"
										type="password"
										value={formData.contrasena}
										onChange={handleChange}
										placeholder="Ingresa tu contraseña"
									/>
								</div>
							</div>

							<button
								type="submit"
								className="auth-submit-button"
								disabled={loading}
							>
								{loading ? "Ingresando..." : "Ingresar"}
							</button>
						</form>

						<p className="auth-switch-link">
							¿No tienes una cuenta aún?{" "}
							<Link to="/registrar">Regístrate aquí</Link>
						</p>
					</div>
				</div>
			</div>
		</div>
	);
}
