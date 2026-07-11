// src/features/circulos/Circulos.jsx

import React, { useState } from "react";

// Datos ficticios (mock data) para que la pantalla muestre círculos
// de estudio de ejemplo mientras no existe conexión real al backend.
const CIRCULOS_INICIALES = [
  {
    id: 1,
    nombreCurso: "Cálculo I",
    creador: "Ana Belén Rodríguez",
    participantesActuales: 4,
    participantesMax: 6,
    horario: "Lunes y Miércoles, 16:00 - 18:00",
    esMiembro: false,
  },
  {
    id: 2,
    nombreCurso: "Desarrollo Web Backend",
    creador: "Jorge Luis Medina",
    participantesActuales: 3,
    participantesMax: 5,
    horario: "Martes y Jueves, 18:00 - 20:00",
    esMiembro: true,
  },
  {
    id: 3,
    nombreCurso: "Bases de Datos II",
    creador: "Camila Torres Vega",
    participantesActuales: 6,
    participantesMax: 6,
    horario: "Viernes, 14:00 - 17:00",
    esMiembro: false,
  },
];

// Estructura inicial vacía para el formulario del modal de creación.
const FORMULARIO_VACIO = {
  nombreCurso: "",
  descripcion: "",
  limiteAlumnos: "",
};

/**
 * Pantalla de Círculos de Estudio de StudyMatch (HU6/HU7).
 * Permite buscar círculos activos por nombre de curso, unirse o
 * salir de un círculo, y crear nuevos círculos mediante un modal.
 */
export default function Circulos() {
  // Lista de círculos de estudio (inicialmente cargada con datos ficticios).
  const [circulos, setCirculos] = useState(CIRCULOS_INICIALES);

  // Termino de busqueda usado para filtrar las tarjetas en tiempo real.
  const [busqueda, setBusqueda] = useState("");

  // Estado de visibilidad del modal de creacion de circulos.
  const [modalAbierto, setModalAbierto] = useState(false);

  // Estado controlado del formulario dentro del modal.
  const [formulario, setFormulario] = useState(FORMULARIO_VACIO);

  // Circulos filtrados en tiempo real segun el texto de busqueda,
  // comparando (sin distinguir mayusculas/minusculas) contra el nombre del curso.
  const circulosFiltrados = circulos.filter((circulo) =>
    circulo.nombreCurso.toLowerCase().includes(busqueda.toLowerCase())
  );

  /**
   * Alterna la membresía del usuario en un círculo especifico:
   * si no es miembro, se une (incrementa el contador de participantes);
   * si ya es miembro, sale del círculo (decrementa el contador).
   *
   * @param {number} id - El identificador del círculo a modificar.
   */
  const alternarMembresia = (id) => {
    setCirculos((prev) =>
      prev.map((circulo) => {
        if (circulo.id !== id) return circulo;

        const nuevoEstadoMiembro = !circulo.esMiembro;
        const nuevoConteo = nuevoEstadoMiembro
          ? circulo.participantesActuales + 1
          : circulo.participantesActuales - 1;

        return {
          ...circulo,
          esMiembro: nuevoEstadoMiembro,
          participantesActuales: nuevoConteo,
        };
      })
    );
  };

  /**
   * Actualiza el estado del formulario del modal a medida que el
   * usuario escribe en cualquiera de sus campos.
   */
  const handleFormularioChange = (e) => {
    const { name, value } = e.target;
    setFormulario((prev) => ({ ...prev, [name]: value }));
  };

  /**
   * Cierra el modal y reinicia el formulario a su estado vacío.
   */
  const cerrarModal = () => {
    setModalAbierto(false);
    setFormulario(FORMULARIO_VACIO);
  };

  /**
   * Simula la creación de un nuevo círculo de estudio: valida los
   * campos, construye el nuevo objeto y lo agrega al array de estado,
   * cerrando el modal de inmediato.
   */
  const handleGuardarCirculo = (e) => {
    e.preventDefault();

    const { nombreCurso, descripcion, limiteAlumnos } = formulario;
    if (!nombreCurso.trim() || !descripcion.trim() || !limiteAlumnos.trim()) {
      return;
    }

    const nuevoCirculo = {
      id: Date.now(),
      nombreCurso: nombreCurso.trim(),
      creador: "Tú",
      participantesActuales: 1,
      participantesMax: Number(limiteAlumnos) || 1,
      horario: "Por definir",
      esMiembro: true,
    };

    setCirculos((prev) => [nuevoCirculo, ...prev]);
    cerrarModal();
  };

  return (
    <div className="min-h-full w-full bg-slate-50 px-4 py-10">
      <div className="max-w-6xl mx-auto">
        {/* Encabezado de la pantalla */}
        <div className="mb-8">
          <h1 className="font-title text-2xl font-bold text-slate-800">
            Círculos de Estudio
          </h1>
          <p className="font-sans text-sm text-slate-500 mt-1">
            Encuentra un círculo activo o crea uno nuevo para tu curso.
          </p>
        </div>

        {/* Barra de busqueda + boton de crear circulo */}
        <div className="flex flex-col sm:flex-row gap-3 mb-8">
          <div className="relative flex-1">
            <span className="absolute inset-y-0 left-0 flex items-center pl-4 text-slate-400">
              <i className="fas fa-magnifying-glass"></i>
            </span>
            <input
              type="text"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              placeholder="Buscar por curso o tema..."
              className="w-full rounded-xl border border-slate-200 pl-11 pr-4 py-3 font-sans text-sm text-slate-800 bg-white shadow-sm outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
            />
          </div>

          <button
            type="button"
            onClick={() => setModalAbierto(true)}
            className="flex items-center justify-center gap-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white transition-all duration-200 px-5 py-3 font-sans text-sm font-semibold shadow-sm whitespace-nowrap"
          >
            <i className="fas fa-plus"></i>
            Crear Círculo
          </button>
        </div>

        {/* Cuadricula de tarjetas de circulos */}
        {circulosFiltrados.length === 0 ? (
          <div className="bg-white border border-slate-100 shadow-sm rounded-2xl px-6 py-16 text-center">
            <i className="fas fa-magnifying-glass text-slate-300 text-2xl mb-3"></i>
            <p className="font-sans text-sm text-slate-500">
              No se encontraron círculos que coincidan con tu búsqueda.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {circulosFiltrados.map((circulo) => {
              const estaLleno =
                circulo.participantesActuales >= circulo.participantesMax &&
                !circulo.esMiembro;

              return (
                <div
                  key={circulo.id}
                  className="bg-white shadow-xl rounded-2xl border border-slate-100 p-6 flex flex-col"
                >
                  <div className="flex-1">
                    <h2 className="font-title text-lg font-bold text-slate-800 mb-1">
                      {circulo.nombreCurso}
                    </h2>
                    <p className="font-sans text-xs text-slate-500 mb-4 flex items-center gap-1.5">
                      <i className="fas fa-user-tie text-slate-400"></i>
                      Creado por {circulo.creador}
                    </p>

                    <div className="space-y-2 mb-5">
                      <div className="flex items-center gap-2 font-sans text-sm text-slate-600">
                        <i className="fas fa-users text-emerald-600 w-4 text-center"></i>
                        {circulo.participantesActuales}/{circulo.participantesMax} alumnos
                      </div>
                      <div className="flex items-center gap-2 font-sans text-sm text-slate-600">
                        <i className="fas fa-clock text-emerald-600 w-4 text-center"></i>
                        {circulo.horario}
                      </div>
                    </div>
                  </div>

                  <button
                    type="button"
                    onClick={() => alternarMembresia(circulo.id)}
                    disabled={estaLleno}
                    className={`w-full flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 font-sans text-sm font-semibold transition-all duration-200 ${
                      circulo.esMiembro
                        ? "bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100"
                        : estaLleno
                        ? "bg-slate-100 text-slate-400 cursor-not-allowed"
                        : "bg-emerald-600 hover:bg-emerald-700 text-white shadow-sm"
                    }`}
                  >
                    {circulo.esMiembro ? (
                      <>
                        <i className="fas fa-circle-check"></i>
                        Miembro
                      </>
                    ) : estaLleno ? (
                      <>
                        <i className="fas fa-lock"></i>
                        Círculo lleno
                      </>
                    ) : (
                      <>
                        <i className="fas fa-right-to-bracket"></i>
                        Unirse al círculo
                      </>
                    )}
                  </button>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Modal de creacion de circulo */}
      {modalAbierto && (
        <div className="fixed inset-0 bg-slate-900/50 flex items-center justify-center px-4 z-50">
          <div className="bg-white w-full max-w-md rounded-2xl shadow-xl border border-slate-100 p-6 sm:p-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="font-title text-lg font-bold text-slate-800">
                Crear nuevo círculo
              </h2>
              <button
                type="button"
                onClick={cerrarModal}
                className="text-slate-400 hover:text-slate-600 transition-colors"
              >
                <i className="fas fa-xmark text-lg"></i>
              </button>
            </div>

            <form onSubmit={handleGuardarCirculo} className="space-y-4" noValidate>
              <div>
                <label className="block font-sans text-sm font-medium text-slate-700 mb-1.5">
                  Nombre del curso
                </label>
                <input
                  type="text"
                  name="nombreCurso"
                  value={formulario.nombreCurso}
                  onChange={handleFormularioChange}
                  placeholder="Ej. Estructuras de Datos"
                  className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 font-sans text-sm text-slate-800 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
                />
              </div>

              <div>
                <label className="block font-sans text-sm font-medium text-slate-700 mb-1.5">
                  Descripción
                </label>
                <textarea
                  name="descripcion"
                  value={formulario.descripcion}
                  onChange={handleFormularioChange}
                  placeholder="Breve descripción de los temas a repasar..."
                  rows={3}
                  className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 font-sans text-sm text-slate-800 outline-none transition resize-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
                ></textarea>
              </div>

              <div>
                <label className="block font-sans text-sm font-medium text-slate-700 mb-1.5">
                  Límite de alumnos
                </label>
                <input
                  type="number"
                  name="limiteAlumnos"
                  value={formulario.limiteAlumnos}
                  onChange={handleFormularioChange}
                  placeholder="Ej. 6"
                  min="1"
                  className="w-full rounded-xl border border-slate-300 px-3.5 py-2.5 font-sans text-sm text-slate-800 outline-none transition focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={cerrarModal}
                  className="flex-1 rounded-xl border border-slate-200 text-slate-600 hover:bg-slate-50 transition-all duration-200 px-4 py-2.5 font-sans text-sm font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex-1 flex items-center justify-center gap-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white transition-all duration-200 px-4 py-2.5 font-sans text-sm font-semibold shadow-sm"
                >
                  <i className="fas fa-floppy-disk"></i>
                  Guardar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
