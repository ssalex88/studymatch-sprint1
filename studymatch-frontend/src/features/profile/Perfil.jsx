import { useState, useEffect } from "react";
import {
	getUserById,
	updateUserProfile,
} from "../../core/services/userService";
import {
	cacheCurrentUserForUi,
	clearCurrentUserCache,
} from "../../core/services/currentUserCache";

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

function mapearPerfilAFormulario(usuario) {
	return {
		carrera: usuario.carrera || "",
		ciclo: usuario.ciclo || "",
		codigoAlumno: usuario.codigoAlumno || "",
		biografia: usuario.biografia || "",
	};
}

/**
 * Vista de perfil académico del usuario autenticado en StudyMatch,
 * conectada al backend de Java puro (HttpServer nativo).
 *
 * Al montarse, carga los datos del usuario desde localStorage ("currentUser")
 * y los refresca desde el backend cuando existe idUsuario.
 * Permite editar Carrera, Ciclo, Código de Alumno y Biografía, y persiste
 * los cambios contra el backend mediante PUT /api/usuarios/{id}.
 */
export default function Perfil() {
	const [usuario, setUsuario] = useState(null);

	const [formData, setFormData] = useState({
		carrera: "",
		ciclo: "",
		codigoAlumno: "",
		biografia: "",
	});

	const [guardando, setGuardando] = useState(false);
	const [exito, setExito] = useState("");
	const [error, setError] = useState("");

	// Carga los datos del usuario logueado y refresca el perfil desde el backend.
	useEffect(() => {
		let componenteActivo = true;
		const datosGuardados = localStorage.getItem("currentUser");

		if (!datosGuardados) {
			return undefined;
		}

		let usuarioParseado;

		try {
			usuarioParseado = JSON.parse(datosGuardados);
		} catch {
			clearCurrentUserCache();
			return undefined;
		}

		const usuarioCacheado = cacheCurrentUserForUi(usuarioParseado);
		setUsuario(usuarioCacheado);
		setFormData(mapearPerfilAFormulario(usuarioCacheado));

		if (usuarioCacheado.idUsuario) {
			getUserById(usuarioCacheado.idUsuario)
				.then((usuarioActualizado) => {
					if (!componenteActivo) {
						return;
					}

					const usuarioActualizadoCache = cacheCurrentUserForUi({
						...usuarioCacheado,
						...usuarioActualizado,
					});
					setUsuario(usuarioActualizadoCache);
					setFormData(mapearPerfilAFormulario(usuarioActualizadoCache));
				})
				.catch((err) => {
					if (!componenteActivo) {
						return;
					}

					setError(
						err.message || "No se pudo refrescar la informacion del perfil.",
					);
				});
		}

		return () => {
			componenteActivo = false;
		};
	}, []);

	const handleChange = (e) => {
		const { name, value } = e.target;
		setFormData((prev) => ({ ...prev, [name]: value }));
	};

	/**
	 * Envía los cambios de perfil al backend mediante PUT y, si la
	 * respuesta es exitosa, actualiza el objeto "currentUser" en
	 * localStorage para que los cambios persistan al recargar la página.
	 */
	const handleSubmit = async (e) => {
		e.preventDefault();
		setError("");
		setExito("");

		if (!usuario) {
			setError("No se encontró información de sesión activa.");
			return;
		}

		if (!formData.carrera.trim() || !formData.ciclo.trim()) {
			setError("Selecciona tanto la carrera como el ciclo antes de guardar.");
			return;
		}

		setGuardando(true);
		try {
			await updateUserProfile(usuario.idUsuario, formData);

			const usuarioActualizado = cacheCurrentUserForUi({
				...usuario,
				...formData,
			});
			setUsuario(usuarioActualizado);

			setExito("Tu información académica se actualizó correctamente.");
		} catch (err) {
			setError(
				err.message || "Ocurrió un error inesperado al guardar los cambios.",
			);
		} finally {
			setGuardando(false);
		}
	};

	if (!usuario) {
		return (
			<div className="min-h-[60vh] w-full flex items-center justify-center bg-slate-50 px-4">
				<p className="font-sans text-sm text-slate-500">
					No se encontró una sesión activa. Por favor inicia sesión nuevamente.
				</p>
			</div>
		);
	}

	return (
		<div className="min-h-full w-full bg-slate-50 px-4 py-10 flex items-start justify-center">
			<div className="w-full max-w-lg">
				<div className="text-center mb-6">
					<div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-emerald-600 shadow-lg shadow-emerald-200 mb-4">
						<i className="fas fa-id-card text-white text-2xl"></i>
					</div>
					<h1 className="font-title text-2xl font-bold text-slate-800">
						Mi perfil académico
					</h1>
					<p className="font-sans text-sm text-slate-500 mt-1">
						Actualiza tu información para que otros estudiantes te conozcan
						mejor
					</p>
				</div>

				<div className="bg-white shadow-xl rounded-2xl px-6 py-8 sm:px-8 border border-slate-100">
					{exito && (
						<div className="bg-green-50 border-l-4 border-green-500 text-green-700 p-4 rounded-r-xl mb-4 text-sm flex items-start gap-2">
							<i className="fas fa-circle-check mt-0.5"></i>
							<span>{exito}</span>
						</div>
					)}

					{error && (
						<div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 rounded-r-xl mb-4 text-sm flex items-start gap-2">
							<i className="fas fa-circle-exclamation mt-0.5"></i>
							<span>{error}</span>
						</div>
					)}

					<div className="flex items-center gap-4 mb-6 pb-6 border-b border-slate-100">
						<div className="w-14 h-14 rounded-full bg-emerald-100 flex items-center justify-center text-emerald-700 font-title font-bold text-lg">
							{usuario.nombreCompleto
								? usuario.nombreCompleto.charAt(0).toUpperCase()
								: "?"}
						</div>
						<div>
							<p className="font-title font-semibold text-slate-800">
								{usuario.nombreCompleto}
							</p>
							<p className="font-sans text-sm text-slate-500">
								{usuario.correoInstitucional}
							</p>
							<span className="inline-flex items-center gap-1 mt-1 text-xs font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700">
								<i className="fas fa-user-shield text-[10px]"></i>
								{usuario.rol}
							</span>
						</div>
					</div>

					<form onSubmit={handleSubmit} className="space-y-5" noValidate>
						<div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
							<div>
								<label
									htmlFor="carrera"
									className="block font-sans text-sm font-medium text-slate-700 mb-1.5"
								>
									Carrera
								</label>
								<div className="relative">
									<span className="absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
										<i className="fas fa-user-graduate"></i>
									</span>
									<select
										id="carrera"
										name="carrera"
										value={formData.carrera}
										onChange={handleChange}
										className="w-full appearance-none rounded-xl border border-slate-300 pl-10 pr-8 py-2.5 font-sans text-sm text-slate-800 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 bg-white"
									>
										<option value="" disabled>
											Selecciona
										</option>
										{CARRERAS.map((c) => (
											<option key={c} value={c}>
												{c}
											</option>
										))}
									</select>
									<span className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400">
										<i className="fas fa-chevron-down text-xs"></i>
									</span>
								</div>
							</div>

							<div>
								<label
									htmlFor="ciclo"
									className="block font-sans text-sm font-medium text-slate-700 mb-1.5"
								>
									Ciclo
								</label>
								<div className="relative">
									<span className="absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
										<i className="fas fa-calendar-days"></i>
									</span>
									<select
										id="ciclo"
										name="ciclo"
										value={formData.ciclo}
										onChange={handleChange}
										className="w-full appearance-none rounded-xl border border-slate-300 pl-10 pr-8 py-2.5 font-sans text-sm text-slate-800 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 bg-white"
									>
										<option value="" disabled>
											Selecciona
										</option>
										{CICLOS.map((c) => (
											<option key={c} value={c}>
												{c}
											</option>
										))}
									</select>
									<span className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400">
										<i className="fas fa-chevron-down text-xs"></i>
									</span>
								</div>
							</div>
						</div>

						<div>
							<label
								htmlFor="codigoAlumno"
								className="block font-sans text-sm font-medium text-slate-700 mb-1.5"
							>
								Código de alumno
							</label>
							<div className="relative">
								<span className="absolute inset-y-0 left-0 flex items-center pl-3.5 text-slate-400">
									<i className="fas fa-hashtag"></i>
								</span>
								<input
									id="codigoAlumno"
									name="codigoAlumno"
									type="text"
									value={formData.codigoAlumno}
									onChange={handleChange}
									placeholder="Ej. 2021045678"
									className="w-full rounded-xl border border-slate-300 pl-10 pr-3.5 py-2.5 font-sans text-sm text-slate-800 placeholder-slate-400 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
								/>
							</div>
						</div>

						<div>
							<label
								htmlFor="biografia"
								className="block font-sans text-sm font-medium text-slate-700 mb-1.5"
							>
								Sobre mí / Biografía
							</label>
							<textarea
								id="biografia"
								name="biografia"
								value={formData.biografia}
								onChange={handleChange}
								placeholder="Cuéntale a tus compañeros un poco sobre ti, tus intereses académicos y en qué temas te gustaría formar círculos de estudio..."
								rows={4}
								className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 font-sans text-sm text-slate-800 placeholder-slate-400 outline-none transition resize-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
							></textarea>
						</div>

						<button
							type="submit"
							disabled={guardando}
							className="w-full flex items-center justify-center gap-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white transition-all duration-200 px-4 py-3 font-sans text-sm font-semibold shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
						>
							{guardando ? (
								<>
									<i className="fas fa-circle-notch fa-spin"></i>
									Guardando...
								</>
							) : (
								<>
									<i className="fas fa-floppy-disk"></i>
									Guardar cambios
								</>
							)}
						</button>
					</form>
				</div>
			</div>
		</div>
	);
}
