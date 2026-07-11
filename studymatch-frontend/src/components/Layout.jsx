// src/components/Layout.jsx

import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Layout.css";

/**
 * Componente envolvente (wrapper) de StudyMatch.
 * Dibuja una barra lateral izquierda estática con el logo, la navegación
 * principal y el botón de cerrar sesión, y deja un área de contenido a la
 * derecha donde se inyecta dinámicamente la pantalla activa a través de
 * la prop `children`.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children - El contenido dinámico a renderizar
 *  dentro del área principal.
 * @param {Function} [props.onLogout] - Callback opcional ejecutado al presionar
 *  "Cerrar Sesión". Si no se provee, se limpia localStorage y se redirige al login.
 */
export default function Layout({ children, onLogout }) {
  const navigate = useNavigate();

  const handleCerrarSesion = () => {
    if (onLogout) {
      onLogout();
      return;
    }

    localStorage.removeItem("currentUser");
    navigate("/login");
  };

  return (
    <div className="layout-container">
      <aside className="layout-sidebar">
        <div className="layout-logo">
          <div className="layout-logo-icon">
            <i className="fas fa-graduation-cap"></i>
          </div>
          <div className="layout-logo-text">
            <span className="layout-logo-title">studymatch</span>
            <span className="layout-logo-subtitle">PLATAFORMA ACADÉMICA</span>
          </div>
        </div>

        <nav className="layout-nav">
          <Link to="/horario" className="layout-nav-link">
            <i className="fas fa-calendar-alt layout-nav-icon"></i>
            <span>Mi Horario Semanal</span>
          </Link>

          <Link to="/circulos" className="layout-nav-link">
            <i className="fas fa-users layout-nav-icon"></i>
            <span>Círculos de Estudio</span>
          </Link>

          <Link to="/perfil" className="layout-nav-link">
            <i className="fas fa-id-card layout-nav-icon"></i>
            <span>Mi Perfil Académico</span>
          </Link>

          <Link to="/admin" className="layout-nav-link">
            <i className="fas fa-shield-alt layout-nav-icon"></i>
            <span>Usuarios y Roles</span>
          </Link>
        </nav>

        <div className="layout-logout">
          <button
            type="button"
            className="layout-logout-button"
            onClick={handleCerrarSesion}
          >
            <i className="fas fa-right-from-bracket"></i>
            <span>Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      <main className="layout-content">{children}</main>
    </div>
  );
}
