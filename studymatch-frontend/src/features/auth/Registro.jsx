// src/features/auth/Registro.jsx

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../../core/services/authService";
import { cacheCurrentUserForUi } from "../../core/services/currentUserCache";
import "./Registro.css";

const CARRERAS = [
	"Ingeniería de Sistemas",
	"Ingeniería Industrial",
	"Ingeniería Civil",
	"Administración de Empresas",
	"Contabilidad",
	"Derecho",
	"Psicología",
	"Medicina Humana",
	"Comunicaciones",
	"Arquitectura",
];

const CICLOS = [
	"1er ciclo",
	"2do ciclo",
	"3er ciclo",
	"4to ciclo",
	"5to ciclo",
	"6to ciclo",
	"7mo ciclo",
	"8vo ciclo",
	"9no ciclo",
	"10mo ciclo",
];

const DOMINIO_INSTITUCIONAL = "@aloe.ulima.edu.pe";
const LONGITUD_MINIMA_CONTRASENA = 8;

/**
 * Pantalla de registro de StudyMatch (HU2).
 * Diseño de panel dividido: mitad izquierda verde informativa,
 * mitad derecha blanca con el formulario de registro.
 */
export default function Registro() {
	const navigate = useNavigate();

	const [formData, setFormData] = useState({
		nombreCompleto: "",
		carrera: "",
		ciclo: "",
		correoInstitucional: "",
		contrasena: "",
	});

	const [error, setError] = useState("");
	const [success, setSuccess] = useState("");
	const [loading, setLoading] = useState(false);

	const handleChange = (e) => {
		const { name, value } = e.target;
		setFormData((prev) => ({ ...prev, [name]: value }));
	};

	const handleSubmit = async (e) => {
		e.preventDefault();
		setError("");
		setSuccess("");

		const { nombreCompleto, carrera, ciclo, correoInstitucional, contrasena } =
			formData;
		if (
			!nombreCompleto.trim() ||
			!carrera.trim() ||
			!ciclo.trim() ||
			!correoInstitucional.trim() ||
			!contrasena.trim()
		) {
			setError("Por favor completa todos los campos antes de continuar.");
			return;
		}

		if (!correoInstitucional.trim().toLowerCase().endsWith(DOMINIO_INSTITUCIONAL)) {
			setError(
				`Usa tu correo institucional con dominio ${DOMINIO_INSTITUCIONAL}.`,
			);
			return;
		}

		if (contrasena.trim().length < LONGITUD_MINIMA_CONTRASENA) {
			setError(
				`La contraseña debe tener al menos ${LONGITUD_MINIMA_CONTRASENA} caracteres.`,
			);
			return;
		}

		setLoading(true);
		try {
			const respuesta = await registerUser(formData);
			cacheCurrentUserForUi(respuesta);
			setSuccess("¡Cuenta creada con éxito! Te redirigiremos a tu perfil...");

			setTimeout(() => {
				navigate("/perfil");
			}, 2000);
		} catch (err) {
			setError(
				err.message || "Ocurrió un error inesperado al registrar la cuenta.",
			);
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
							Crea tu cuenta con tu correo institucional y empieza a conectar
							con compañeros de tu misma carrera y ciclo.
						</p>
					</div>
				</div>

				<div className="auth-panel auth-panel-white">
					<div className="auth-form-wrapper">
						<h2 className="auth-form-title">Crear cuenta</h2>
						<p className="auth-form-subtitle">
							Completa tus datos para registrarte en StudyMatch.
						</p>

						{error && (
							<div className="auth-banner auth-banner-error">
								<i className="fas fa-circle-exclamation"></i>
								<span>{error}</span>
							</div>
						)}

						{success && (
							<div className="auth-banner auth-banner-success">
								<i className="fas fa-circle-check"></i>
								<span>{success}</span>
							</div>
						)}

						<form onSubmit={handleSubmit} className="auth-form">
							<div className="auth-input-group">
								<label htmlFor="nombreCompleto">Nombre completo</label>
								<div className="auth-input-wrapper">
									<i className="fas fa-user auth-input-icon"></i>
									<input
										id="nombreCompleto"
										name="nombreCompleto"
										type="text"
										value={formData.nombreCompleto}
										onChange={handleChange}
										placeholder="Ej. María Fernanda Torres"
									/>
								</div>
							</div>

							<div className="auth-input-row">
								<div className="auth-input-group">
									<label htmlFor="carrera">Carrera</label>
									<div className="auth-input-wrapper">
										<i className="fas fa-user-graduate auth-input-icon"></i>
										<select
											id="carrera"
											name="carrera"
											value={formData.carrera}
											onChange={handleChange}
										>
											<option value="" disabled>
												Selecciona
											</option>
											{CARRERAS.map((carrera) => (
												<option key={carrera} value={carrera}>
													{carrera}
												</option>
											))}
										</select>
										<i className="fas fa-chevron-down auth-select-chevron"></i>
									</div>
								</div>

								<div className="auth-input-group">
									<label htmlFor="ciclo">Ciclo</label>
									<div className="auth-input-wrapper">
										<i className="fas fa-calendar-days auth-input-icon"></i>
										<select
											id="ciclo"
											name="ciclo"
											value={formData.ciclo}
											onChange={handleChange}
										>
											<option value="" disabled>
												Selecciona
											</option>
											{CICLOS.map((ciclo) => (
												<option key={ciclo} value={ciclo}>
													{ciclo}
												</option>
											))}
										</select>
										<i className="fas fa-chevron-down auth-select-chevron"></i>
									</div>
								</div>
							</div>

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
										placeholder="nombre.apellido@aloe.ulima.edu.pe"
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
										placeholder="Mínimo 8 caracteres"
									/>
								</div>
							</div>

							<button
								type="submit"
								className="auth-submit-button"
								disabled={loading}
							>
								{loading ? "Creando cuenta..." : "Crear cuenta"}
							</button>
						</form>

						<p className="auth-switch-link">
							¿Ya tienes una cuenta registrada?{" "}
							<Link to="/login">Inicia sesión aquí</Link>
						</p>
					</div>
				</div>
			</div>
		</div>
	);
}
