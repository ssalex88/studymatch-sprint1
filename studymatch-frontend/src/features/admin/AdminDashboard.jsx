// src/features/admin/AdminDashboard.jsx

import { useState, useEffect } from "react";
import { getAllUsers, updateUserRole } from "../../core/services/userService";

const ROLES_DISPONIBLES = [
	"Administrador",
	"Profesor",
	"Organizador",
	"Estudiante",
];

/**
 * Devuelve las clases de Tailwind para el badge de un rol determinado.
 *
 * @param {string} rol - El rol del usuario ("Administrador", "Estudiante", etc.).
 * @returns {string} Las clases de color correspondientes al badge.
 */
function obtenerClasesRol(rol) {
	if (rol === "Administrador") {
		return "bg-purple-100 text-purple-700";
	}

	if (rol === "Profesor") {
		return "bg-sky-100 text-sky-700";
	}

	if (rol === "Organizador") {
		return "bg-amber-100 text-amber-700";
	}

	return "bg-emerald-100 text-emerald-700";
}

/**
 * Panel de administración de StudyMatch.
 * Regla de Seguridad (HU4): solo un usuario con rol "Administrador"
 * almacenado en localStorage puede ver la tabla de usuarios registrados.
 * Cualquier otro caso bloquea la renderización y muestra una pantalla
 * de "Acceso Denegado - Error 403".
 */
export default function AdminDashboard() {
	const [usuarioActual, setUsuarioActual] = useState(null);
	const [verificado, setVerificado] = useState(false);
	const [usuarios, setUsuarios] = useState([]);
	const [cargando, setCargando] = useState(false);
	const [actualizandoRolId, setActualizandoRolId] = useState(null);
	const [error, setError] = useState("");
	const [exito, setExito] = useState("");

	useEffect(() => {
		const datosGuardados = localStorage.getItem("currentUser");

		try {
			const usuarioParseado = datosGuardados
				? JSON.parse(datosGuardados)
				: null;
			setUsuarioActual(usuarioParseado);
		} catch {
			localStorage.removeItem("currentUser");
			setUsuarioActual(null);
		} finally {
			setVerificado(true);
		}
	}, []);

	useEffect(() => {
		if (!verificado) return;

		const esAdministrador =
			usuarioActual && usuarioActual.rol === "Administrador";
		if (!esAdministrador) return;

		const cargarUsuarios = async () => {
			setCargando(true);
			setError("");
			setExito("");
			try {
				const lista = await getAllUsers();
				setUsuarios(lista);
			} catch (err) {
				setError(err.message || "No se pudo cargar la lista de usuarios.");
			} finally {
				setCargando(false);
			}
		};

		cargarUsuarios();
	}, [verificado, usuarioActual]);

	const handleCambiarRol = async (usuario, nuevoRol) => {
		if (!usuario || usuario.rol === nuevoRol) return;

		setActualizandoRolId(usuario.idUsuario);
		setError("");
		setExito("");

		try {
			await updateUserRole(usuario.idUsuario, nuevoRol);

			setUsuarios((usuariosActuales) =>
				usuariosActuales.map((item) =>
					item.idUsuario === usuario.idUsuario
						? { ...item, rol: nuevoRol }
						: item,
				),
			);

			if (usuarioActual && usuarioActual.idUsuario === usuario.idUsuario) {
				const usuarioActualizado = { ...usuarioActual, rol: nuevoRol };
				localStorage.setItem("currentUser", JSON.stringify(usuarioActualizado));
				setUsuarioActual(usuarioActualizado);
			}

			setExito(`Rol de ${usuario.nombreCompleto} actualizado a ${nuevoRol}.`);
		} catch (err) {
			setError(err.message || "No se pudo actualizar el rol del usuario.");
		} finally {
			setActualizandoRolId(null);
		}
	};

	if (!verificado) {
		return null;
	}

	const esAdministrador =
		usuarioActual && usuarioActual.rol === "Administrador";

	if (!esAdministrador) {
		return (
			<div className="min-h-full w-full flex items-center justify-center bg-slate-50 px-4 py-16">
				<div className="w-full max-w-md text-center bg-white border border-slate-100 shadow-xl rounded-2xl px-8 py-12">
					<div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-red-100 mb-6">
						<i className="fas fa-lock text-red-600 text-3xl"></i>
					</div>
					<h1 className="font-title text-2xl font-bold text-slate-800 mb-2">
						Acceso Denegado
					</h1>
					<p className="font-sans text-sm font-semibold text-red-600 mb-3">
						Error 403
					</p>
					<p className="font-sans text-sm text-slate-500">
						No cuentas con los permisos necesarios para visualizar este panel.
						Esta sección está reservada exclusivamente para usuarios con rol de
						Administrador.
					</p>
				</div>
			</div>
		);
	}

	return (
		<div className="min-h-full w-full bg-slate-50 px-4 py-10">
			<div className="max-w-6xl mx-auto">
				<div className="mb-8">
					<h1 className="font-title text-2xl font-bold text-slate-800">
						Panel de administración
					</h1>
					<p className="font-sans text-sm text-slate-500 mt-1">
						Gestiona y supervisa a todos los usuarios registrados en StudyMatch
					</p>
				</div>

				<div className="bg-white shadow-xl rounded-2xl border border-slate-100 overflow-hidden">
					{exito && (
						<div className="bg-green-50 border-l-4 border-green-500 text-green-700 p-4 rounded-r-xl m-4 text-sm flex items-start gap-2">
							<i className="fas fa-circle-check mt-0.5"></i>
							<span>{exito}</span>
						</div>
					)}

					{error && (
						<div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 rounded-r-xl m-4 text-sm flex items-start gap-2">
							<i className="fas fa-circle-exclamation mt-0.5"></i>
							<span>{error}</span>
						</div>
					)}

					{cargando ? (
						<div className="flex flex-col items-center justify-center py-16 gap-3">
							<i className="fas fa-circle-notch fa-spin text-emerald-600 text-2xl"></i>
							<p className="font-sans text-sm text-slate-500">Cargando...</p>
						</div>
					) : (
						<div className="overflow-x-auto">
							<table className="w-full text-left">
								<thead className="bg-slate-50 border-b border-slate-200">
									<tr>
										<th className="px-6 py-3 font-sans text-xs font-semibold uppercase tracking-wider text-slate-500">
											ID
										</th>
										<th className="px-6 py-3 font-sans text-xs font-semibold uppercase tracking-wider text-slate-500">
											Nombre
										</th>
										<th className="px-6 py-3 font-sans text-xs font-semibold uppercase tracking-wider text-slate-500">
											Correo
										</th>
										<th className="px-6 py-3 font-sans text-xs font-semibold uppercase tracking-wider text-slate-500">
											Carrera
										</th>
										<th className="px-6 py-3 font-sans text-xs font-semibold uppercase tracking-wider text-slate-500">
											Ciclo
										</th>
										<th className="px-6 py-3 font-sans text-xs font-semibold uppercase tracking-wider text-slate-500">
											Rol
										</th>
									</tr>
								</thead>
								<tbody className="divide-y divide-slate-100">
									{usuarios.length === 0 ? (
										<tr>
											<td
												colSpan={6}
												className="px-6 py-10 text-center font-sans text-sm text-slate-400"
											>
												No hay usuarios registrados todavía.
											</td>
										</tr>
									) : (
										usuarios.map((u) => (
											<tr
												key={u.idUsuario}
												className="hover:bg-slate-50 transition-colors"
											>
												<td className="px-6 py-4 font-sans text-sm text-slate-500">
													#{u.idUsuario}
												</td>
												<td className="px-6 py-4 font-sans text-sm font-medium text-slate-800">
													{u.nombreCompleto}
												</td>
												<td className="px-6 py-4 font-sans text-sm text-slate-600">
													{u.correoInstitucional}
												</td>
												<td className="px-6 py-4 font-sans text-sm text-slate-600">
													{u.carrera || "—"}
												</td>
												<td className="px-6 py-4 font-sans text-sm text-slate-600">
													{u.ciclo || "—"}
												</td>
												<td className="px-6 py-4">
													<div className="flex min-w-44 flex-col gap-2">
														<span
															className={`inline-flex w-fit items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full ${obtenerClasesRol(
																u.rol,
															)}`}
														>
															<i className="fas fa-circle text-[6px]"></i>
															{u.rol}
														</span>
														<label
															className="sr-only"
															htmlFor={`rol-${u.idUsuario}`}
														>
															Cambiar rol de {u.nombreCompleto}
														</label>
														<select
															id={`rol-${u.idUsuario}`}
															value={u.rol || ""}
															disabled={actualizandoRolId === u.idUsuario}
															onChange={(event) =>
																handleCambiarRol(u, event.target.value)
															}
															className="rounded-lg border border-slate-300 bg-white px-3 py-2 font-sans text-xs font-medium text-slate-700 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100 disabled:cursor-not-allowed disabled:opacity-60"
														>
															{!ROLES_DISPONIBLES.includes(u.rol) && u.rol && (
																<option value={u.rol}>{u.rol}</option>
															)}
															{ROLES_DISPONIBLES.map((rol) => (
																<option key={rol} value={rol}>
																	{rol}
																</option>
															))}
														</select>
														{actualizandoRolId === u.idUsuario && (
															<span className="font-sans text-xs text-slate-400">
																Actualizando rol...
															</span>
														)}
													</div>
												</td>
											</tr>
										))
									)}
								</tbody>
							</table>
						</div>
					)}
				</div>
			</div>
		</div>
	);
}
