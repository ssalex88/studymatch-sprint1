package net.studymatch.api.controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.studymatch.api.config.CorsHelper;
import net.studymatch.api.entity.Usuario;
import net.studymatch.api.service.UsuarioService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controlador HTTP encargado de la gestion general de usuarios de StudyMatch:
 * - GET  /api/usuarios          -> listar todos los usuarios
 * - PUT  /api/usuarios/{id}     -> actualizar el perfil academico de un usuario
 * - POST /api/usuarios/{id}/rol -> cambiar el rol de un usuario
 */
public class UsuarioController implements HttpHandler {

    private UsuarioService usuarioService = new UsuarioService();
    private Gson gson = new Gson();

    // Coincide con "/api/usuarios/{id}" (sin sufijo adicional despues del id).
    private static final Pattern PATRON_USUARIO_POR_ID =
            Pattern.compile("^/api/usuarios/(\\d+)$");

    // Coincide con "/api/usuarios/{id}/rol".
    private static final Pattern PATRON_ROL_USUARIO =
            Pattern.compile("^/api/usuarios/(\\d+)/rol$");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (CorsHelper.manejarPreflight(exchange)) {
            return;
        }

        CorsHelper.aplicarCors(exchange);

        String metodo = exchange.getRequestMethod();
        String ruta = exchange.getRequestURI().getPath();

        try {
            Matcher matcherRol = PATRON_ROL_USUARIO.matcher(ruta);
            Matcher matcherPorId = PATRON_USUARIO_POR_ID.matcher(ruta);

            if ("GET".equalsIgnoreCase(metodo) && "/api/usuarios".equals(ruta)) {
                manejarListarUsuarios(exchange);
            } else if (("POST".equalsIgnoreCase(metodo) || "PATCH".equalsIgnoreCase(metodo))
                    && matcherRol.matches()) {
                int idUsuario = Integer.parseInt(matcherRol.group(1));
                manejarCambioDeRol(exchange, idUsuario);
            } else if ("PUT".equalsIgnoreCase(metodo) && matcherPorId.matches()) {
                int idUsuario = Integer.parseInt(matcherPorId.group(1));
                manejarActualizarPerfil(exchange, idUsuario);
            } else {
                enviarError(exchange, 404, "Ruta no encontrada.");
            }
        } catch (NumberFormatException e) {
            enviarError(exchange, 400, "El identificador de usuario debe ser numerico.");
        } catch (IllegalArgumentException e) {
            enviarError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            enviarError(exchange, 500, "Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Maneja la ruta GET /api/usuarios.
     * Recupera todos los usuarios registrados y los serializa a JSON.
     */
    private void manejarListarUsuarios(HttpExchange exchange) throws Exception {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        enviarRespuesta(exchange, 200, gson.toJson(usuarios));
    }

    /**
     * Maneja la ruta PUT /api/usuarios/{id}.
     * Lee el cuerpo de la peticion, lo mapea a un Usuario con Gson,
     * y actualiza unicamente los campos de perfil academico
     * (carrera, ciclo, codigoAlumno, biografia).
     */
    private void manejarActualizarPerfil(HttpExchange exchange, int idUsuario) throws Exception {
        Usuario datosRecibidos = leerCuerpo(exchange, Usuario.class);

        if (datosRecibidos == null) {
            enviarError(exchange, 400, "El cuerpo de la peticion no es un JSON valido.");
            return;
        }

        boolean actualizado = usuarioService.modificarPerfil(
                idUsuario,
                datosRecibidos.getCarrera(),
                datosRecibidos.getCiclo(),
                datosRecibidos.getCodigoAlumno(),
                datosRecibidos.getBiografia()
        );

        if (actualizado) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Perfil actualizado correctamente.");
            enviarRespuesta(exchange, 200, gson.toJson(respuesta));
        } else {
            enviarError(exchange, 400, "No se pudo actualizar el perfil. Verifica el ID del usuario.");
        }
    }

    /**
     * Maneja la ruta POST/PATCH /api/usuarios/{id}/rol.
     * Lee el cuerpo de la peticion (ej. {"rol": "Administrador"})
     * y actualiza el rol del usuario indicado.
     */
    private void manejarCambioDeRol(HttpExchange exchange, int idUsuario) throws Exception {
        Map cuerpoJson = leerCuerpo(exchange, Map.class);

        if (cuerpoJson == null || cuerpoJson.get("rol") == null) {
            enviarError(exchange, 400, "Debes indicar el campo 'rol' en el cuerpo de la peticion.");
            return;
        }

        String nuevoRol = String.valueOf(cuerpoJson.get("rol"));

        boolean actualizado = usuarioService.cambiarRolUsuario(idUsuario, nuevoRol);

        if (actualizado) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Rol actualizado correctamente.");
            enviarRespuesta(exchange, 200, gson.toJson(respuesta));
        } else {
            enviarError(exchange, 400, "No se pudo actualizar el rol. Verifica el ID del usuario.");
        }
    }

    /**
     * Lee el cuerpo de la peticion HTTP y lo deserializa al tipo indicado usando Gson.
     *
     * @param exchange el intercambio HTTP actual.
     * @param clase    la clase destino a la que se deserializara el JSON.
     * @param <T>      el tipo generico del resultado.
     * @return el objeto deserializado a partir del cuerpo de la peticion.
     * @throws IOException si ocurre un error al leer el cuerpo de la peticion.
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
     *
     * @param exchange   el intercambio HTTP actual.
     * @param codigo     el codigo de estado HTTP a responder.
     * @param jsonCuerpo el cuerpo de la respuesta en formato JSON.
     * @throws IOException si ocurre un error al escribir la respuesta.
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
     * Envia una respuesta HTTP de error, con un cuerpo JSON que contiene
     * el mensaje descriptivo del problema.
     *
     * @param exchange el intercambio HTTP actual.
     * @param codigo   el codigo de estado HTTP de error a responder.
     * @param mensaje  el mensaje de error a incluir en el cuerpo JSON.
     * @throws IOException si ocurre un error al escribir la respuesta.
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
