package net.studymatch.api.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.studymatch.api.config.CorsHelper;
import net.studymatch.api.dto.AuthResponseDTO;
import net.studymatch.api.dto.LoginRequestDTO;
import net.studymatch.api.dto.UsuarioRegistroDTO;
import net.studymatch.api.service.SessionService;
import net.studymatch.api.service.UsuarioService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Controlador HTTP encargado de la autenticacion de StudyMatch:
 * - POST /api/auth/registrar -> registrar un nuevo usuario
 * - POST /api/auth/login     -> iniciar sesion
 * - POST /api/auth/logout    -> cerrar sesion
 */
public class AuthController implements HttpHandler {

    private static final String RUTA_REGISTRO = "/api/auth/registrar";
    private static final String RUTA_LOGIN = "/api/auth/login";
    private static final String RUTA_LOGOUT = "/api/auth/logout";
    private static final int SESSION_COOKIE_MAX_AGE_SECONDS = 8 * 60 * 60;
    private static final Pattern PATRON_EMAIL_GENERICO =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private UsuarioService usuarioService = new UsuarioService();
    private SessionService sessionService = new SessionService();
    private Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsHelper.manejarPreflight(exchange)) {
            return;
        }

        CorsHelper.aplicarCors(exchange);

        String metodo = exchange.getRequestMethod();
        String ruta = exchange.getRequestURI().getPath();

        try {
            if (RUTA_REGISTRO.equals(ruta)) {
                if (!"POST".equalsIgnoreCase(metodo)) {
                    enviarMetodoNoPermitido(exchange);
                    return;
                }
                manejarRegistro(exchange);
            } else if (RUTA_LOGIN.equals(ruta)) {
                if (!"POST".equalsIgnoreCase(metodo)) {
                    enviarMetodoNoPermitido(exchange);
                    return;
                }
                manejarLogin(exchange);
            } else if (RUTA_LOGOUT.equals(ruta)) {
                if (!"POST".equalsIgnoreCase(metodo)) {
                    enviarMetodoNoPermitido(exchange);
                    return;
                }
                manejarLogout(exchange);
            } else {
                enviarError(exchange, 404, "Ruta no encontrada.");
            }
        } catch (JsonSyntaxException e) {
            enviarError(exchange, 400, "El cuerpo de la peticion no es un JSON valido.");
        } catch (IllegalArgumentException e) {
            enviarError(exchange, 400, e.getMessage());
        } catch (SecurityException e) {
            enviarError(exchange, 401, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            enviarError(exchange, 500, "Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Maneja la ruta POST /api/auth/registrar.
     */
    private void manejarRegistro(HttpExchange exchange) throws Exception {
        UsuarioRegistroDTO dto = leerCuerpo(exchange, UsuarioRegistroDTO.class);
        validarRegistro(dto);

        AuthResponseDTO respuesta = usuarioService.registrarUsuario(dto);
        adjuntarCookieSesion(exchange, respuesta);
        enviarRespuesta(exchange, 201, gson.toJson(respuesta));
    }

    /**
     * Maneja la ruta POST /api/auth/login.
     */
    private void manejarLogin(HttpExchange exchange) throws Exception {
        LoginRequestDTO dto = leerCuerpo(exchange, LoginRequestDTO.class);
        validarLogin(dto);

        AuthResponseDTO respuesta = usuarioService.iniciarSesion(dto);
        adjuntarCookieSesion(exchange, respuesta);
        enviarRespuesta(exchange, 200, gson.toJson(respuesta));
    }

    /**
     * Maneja la ruta POST /api/auth/logout.
     */
    private void manejarLogout(HttpExchange exchange) throws IOException {
        String token = sessionService.obtenerTokenDeSesion(exchange);
        sessionService.eliminarSesion(token);
        limpiarCookieSesion(exchange);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Sesion cerrada correctamente.");
        enviarRespuesta(exchange, 200, gson.toJson(respuesta));
    }

    /**
     * Valida los campos minimos requeridos para registrar un usuario.
     * La validacion de dominio institucional queda fuera de esta slice porque
     * el dominio esperado no esta definido; solo se valida formato generico.
     */
    private void validarRegistro(UsuarioRegistroDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion no puede estar vacio.");
        }
        validarCampoObligatorio(dto.getNombreCompleto(), "nombreCompleto");
        validarCorreo(dto.getCorreoInstitucional());
        validarCampoObligatorio(dto.getContrasena(), "contrasena");
        validarCampoObligatorio(dto.getCarrera(), "carrera");
        validarCampoObligatorio(dto.getCiclo(), "ciclo");
    }

    /**
     * Valida los campos minimos requeridos para iniciar sesion.
     */
    private void validarLogin(LoginRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la peticion no puede estar vacio.");
        }
        validarCorreo(dto.getCorreoInstitucional());
        validarCampoObligatorio(dto.getContrasena(), "contrasena");
    }

    private void validarCorreo(String correo) {
        validarCampoObligatorio(correo, "correoInstitucional");
        if (!PATRON_EMAIL_GENERICO.matcher(correo.trim()).matches()) {
            throw new IllegalArgumentException("El correo institucional debe tener un formato valido.");
        }
    }

    private void validarCampoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio.");
        }
    }

    private void adjuntarCookieSesion(HttpExchange exchange, AuthResponseDTO respuesta) {
        String token = respuesta.getSessionToken();
        if (token != null && !token.trim().isEmpty()) {
            exchange.getResponseHeaders().add("Set-Cookie", construirCookieSesion(token));
            respuesta.setSessionToken(null);
        }
    }

    private void limpiarCookieSesion(HttpExchange exchange) {
        exchange.getResponseHeaders().add(
                "Set-Cookie",
                SessionService.SESSION_COOKIE_NAME + "=; HttpOnly; SameSite=Lax; Path=/; Max-Age=0"
        );
    }

    private String construirCookieSesion(String token) {
        return SessionService.SESSION_COOKIE_NAME + "=" + token
                + "; HttpOnly; SameSite=Lax; Path=/; Max-Age=" + SESSION_COOKIE_MAX_AGE_SECONDS;
    }

    /**
     * Lee el cuerpo de la peticion HTTP y lo deserializa al tipo indicado usando Gson.
     */
    private <T> T leerCuerpo(HttpExchange exchange, Class<T> clase) throws IOException {
        StringBuilder cuerpo = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                cuerpo.append(linea);
            }
        }

        if (cuerpo.length() == 0) {
            return null;
        }

        return gson.fromJson(cuerpo.toString(), clase);
    }

    /**
     * Envia una respuesta HTTP exitosa con el codigo de estado y cuerpo JSON indicados.
     */
    private void enviarRespuesta(HttpExchange exchange, int codigo, String jsonCuerpo) throws IOException {
        byte[] bytesRespuesta = jsonCuerpo.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(codigo, bytesRespuesta.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytesRespuesta);
            os.flush();
        }
    }

    /**
     * Envia una respuesta 405 cuando la ruta existe pero el metodo HTTP no esta permitido.
     */
    private void enviarMetodoNoPermitido(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Allow", "POST, OPTIONS");
        enviarError(exchange, 405, "Metodo no permitido. Usa POST.");
    }

    /**
     * Envia una respuesta HTTP de error en formato JSON.
     */
    private void enviarError(HttpExchange exchange, int codigo, String mensaje) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", mensaje);

        String jsonError = gson.toJson(error);
        byte[] bytesError = jsonError.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(codigo, bytesError.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytesError);
            os.flush();
        }
    }
}
