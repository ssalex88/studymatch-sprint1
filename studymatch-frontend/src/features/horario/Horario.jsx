// src/features/horario/Horario.jsx

import React, { useState } from "react";

// Franjas horarias disponibles para seleccionar (filas de la grilla).
const HORAS = ["08:00 - 10:00", "10:00 - 12:00", "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00"];

// Días de la semana disponibles para seleccionar (columnas de la grilla).
const DIAS = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes"];

/**
 * Construye la clave única que identifica una celda de la grilla,
 * combinando el día y la franja horaria (ej. "Lunes-08:00 - 10:00").
 *
 * @param {string} dia - El día de la semana.
 * @param {string} hora - La franja horaria.
 * @returns {string} La clave única de la celda.
 */
function construirClave(dia, hora) {
  return `${dia}-${hora}`;
}

/**
 * Pantalla de gestión de horario disponible de StudyMatch (HU5).
 * Permite al usuario marcar los bloques de día/hora en los que está
 * disponible para formar parte de círculos de estudio.
 */
export default function Horario() {
  // Objeto donde cada clave "Dia-Hora" representa si esa celda está
  // seleccionada (true) o no (false/ausente). Usamos un objeto en vez
  // de un array para poder alternar (toggle) cada celda en O(1).
  const [seleccion, setSeleccion] = useState({});

  const [guardando, setGuardando] = useState(false);
  const [exito, setExito] = useState("");

  /**
   * Alterna el estado de disponibilidad de una celda especifica
   * (dia + franja horaria) al hacer clic sobre ella.
   *
   * @param {string} dia - El día de la semana de la celda.
   * @param {string} hora - La franja horaria de la celda.
   */
  const alternarCelda = (dia, hora) => {
    const clave = construirClave(dia, hora);
    setSeleccion((prev) => ({
      ...prev,
      [clave]: !prev[clave],
    }));

    // Cualquier cambio manual oculta el banner de éxito anterior,
    // ya que el horario mostrado en pantalla ya no coincide con
    // lo último "guardado".
    setExito("");
  };

  /**
   * Simula el envío del horario seleccionado al backend.
   * Activa un estado de carga (spinner) y, tras un pequeño retraso,
   * muestra un banner de éxito confirmando la actualización.
   */
  const handleGuardarHorario = () => {
    setGuardando(true);
    setExito("");

    // Simulación de una llamada asíncrona real (por ahora sin backend).
    setTimeout(() => {
      setGuardando(false);
      setExito("¡Tu horario de disponibilidad se actualizó correctamente!");
    }, 1500);
  };

  return (
    <div className="min-h-full w-full bg-slate-50 px-4 py-10">
      <div className="max-w-5xl mx-auto">
        {/* Encabezado de la pantalla */}
        <div className="mb-8">
          <h1 className="font-title text-2xl font-bold text-slate-800">
            Mi Horario de Disponibilidad
          </h1>
          <p className="font-sans text-sm text-slate-500 mt-1">
            Selecciona los bloques de día y hora en los que estás disponible
            para conectar con tus círculos de estudio.
          </p>
        </div>

        <div className="bg-white shadow-xl rounded-2xl border border-slate-100 px-6 py-8 sm:px-8">
          {/* Banner de éxito, visible solo tras guardar correctamente */}
          {exito && (
            <div className="bg-green-50 border-l-4 border-green-500 text-green-700 p-4 rounded-r-xl mb-6 text-sm flex items-start gap-2">
              <i className="fas fa-circle-check mt-0.5"></i>
              <span>{exito}</span>
            </div>
          )}

          {/* Leyenda de estados de la grilla */}
          <div className="flex items-center gap-6 mb-6 font-sans text-xs text-slate-500">
            <div className="flex items-center gap-2">
              <span className="w-4 h-4 rounded bg-emerald-600 inline-block"></span>
              Disponible
            </div>
            <div className="flex items-center gap-2">
              <span className="w-4 h-4 rounded bg-slate-100 border border-slate-200 inline-block"></span>
              No disponible
            </div>
          </div>

          {/* Grilla/Matriz de horario: filas = horas, columnas = dias */}
          <div className="overflow-x-auto">
            <table className="w-full border-separate border-spacing-2">
              <thead>
                <tr>
                  {/* Celda vacía superior izquierda, sobre la columna de horas */}
                  <th className="w-32"></th>
                  {DIAS.map((dia) => (
                    <th
                      key={dia}
                      className="font-sans text-xs font-semibold uppercase tracking-wider text-slate-500 pb-2"
                    >
                      {dia}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {HORAS.map((hora) => (
                  <tr key={hora}>
                    {/* Etiqueta de la franja horaria (columna izquierda) */}
                    <td className="font-sans text-xs font-medium text-slate-500 whitespace-nowrap pr-2">
                      {hora}
                    </td>

                    {DIAS.map((dia) => {
                      const clave = construirClave(dia, hora);
                      const estaSeleccionada = Boolean(seleccion[clave]);

                      return (
                        <td key={clave}>
                          <button
                            type="button"
                            onClick={() => alternarCelda(dia, hora)}
                            aria-pressed={estaSeleccionada}
                            className={`w-full h-14 rounded-xl border transition-all duration-200 flex items-center justify-center font-sans text-xs font-medium ${
                              estaSeleccionada
                                ? "bg-emerald-600 border-emerald-600 text-white shadow-sm"
                                : "bg-slate-50 border-slate-200 text-slate-400 hover:border-emerald-300 hover:text-emerald-600"
                            }`}
                          >
                            {estaSeleccionada ? (
                              <i className="fas fa-calendar-check text-sm"></i>
                            ) : (
                              <i className="fas fa-plus text-xs opacity-40"></i>
                            )}
                          </button>
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Boton de guardado, con estado de carga simulado */}
          <button
            type="button"
            onClick={handleGuardarHorario}
            disabled={guardando}
            className="w-full mt-8 flex items-center justify-center gap-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white transition-all duration-200 px-4 py-3 font-sans text-sm font-semibold shadow-sm disabled:cursor-not-allowed disabled:opacity-60"
          >
            {guardando ? (
              <>
                <i className="fas fa-circle-notch fa-spin"></i>
                Guardando...
              </>
            ) : (
              <>
                <i className="fas fa-floppy-disk"></i>
                Guardar Horario
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
