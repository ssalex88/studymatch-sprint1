// src/App.jsx

import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Horario from "./features/horario/Horario";
import Layout from "./components/Layout";
import Perfil from "./features/profile/Perfil";
import Admin from "./features/admin/AdminDashboard";
import Circulos from "./features/circulos/Circulos";
import Login from "./features/auth/Login"; 
import Registro from "./features/auth/Registro";


/**
 * Componente raíz de StudyMatch.
 * Configura el enrutamiento principal de la aplicación usando react-router-dom:
 * - Rutas públicas (login, registro) sin la barra lateral.
 * - Rutas privadas (perfil, horario, circulos, admin) envueltas en <Layout>,
 *   para que compartan la barra lateral verde y el área de contenido.
 * - Una ruta raíz ("/") que redirige automáticamente a "/login".
 */
export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Ruta raíz: redirige automáticamente a /login */}
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* Rutas públicas (sin Layout) */}
        <Route path="/login" element={<Login />} />
        <Route path="/registrar" element={<Registro />} />

        {/* Rutas privadas (envueltas en Layout) */}
        <Route
          path="/perfil"
          element={
            <Layout>
              <Perfil />
            </Layout>
          }
        />
        <Route
          path="/horario"
          element={
            <Layout>
              <Horario />
            </Layout>
          }
        />
        <Route
          path="/circulos"
          element={
            <Layout>
              <Circulos />
            </Layout>
          }
        />
        <Route
          path="/admin"
          element={
            <Layout>
              <Admin />
            </Layout>
          }
        />

        {/* Cualquier otra ruta desconocida también redirige a /login */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
