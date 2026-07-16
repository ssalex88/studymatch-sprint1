package net.studymatch.api.service;

import com.sun.net.httpserver.HttpExchange;
import net.studymatch.api.entity.Usuario;
import net.studymatch.api.repository.UsuarioRepository;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Servicio simple de sesiones en memoria para Sprint 1.
 *
 * <p>Limitacion intencional: las sesiones se pierden al reiniciar el backend
 * y no son aptas para produccion. La sesion primaria viaja en una cookie
 * HttpOnly; Authorization: Bearer queda como compatibilidad de bajo costo.</p>
 */
public class SessionService {

    public static final String SESSION_COOKIE_NAME = "studyMatchSession";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder TOKEN_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final ConcurrentMap<String, Integer> SESIONES = new ConcurrentHashMap<>();
    private static final int TOKEN_BYTES = 32;

    private UsuarioRepository usuarioRepository = new UsuarioRepository();

    /**
     * Crea una sesion para el usuario indicado y devuelve el token asociado.
     *
     * @param usuario el usuario autenticado o recien registrado.
     * @return token opaco para enviar en cookie HttpOnly.
     */
    public String crearSesion(Usuario usuario) {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        String token = TOKEN_ENCODER.encodeToString(bytes);

        SESIONES.put(token, usuario.getIdUsuario());
        return token;
    }

    /**
     * Invalida una sesion activa si el token existe en el almacen en memoria.
     *
     * @param token token opaco asociado a la sesion.
     */
    public void eliminarSesion(String token) {
        if (token != null && !token.trim().isEmpty()) {
            SESIONES.remove(token.trim());
        }
    }

    /**
     * Resuelve el usuario actual a partir de la cookie de sesion HttpOnly.
     * Si la cookie no existe, intenta Authorization: Bearer como fallback.
     *
     * @param exchange la peticion HTTP actual.
     * @return el usuario autenticado sin contrasena, o null si falta o no es valido.
     * @throws SQLException si falla la consulta del usuario vigente.
     */
    public Usuario obtenerUsuarioAutenticado(HttpExchange exchange) throws SQLException {
        String token = obtenerTokenDeSesion(exchange);
        if (token == null) {
            return null;
        }

        Integer idUsuario = SESIONES.get(token);
        if (idUsuario == null) {
            return null;
        }

        Usuario usuario = usuarioRepository.buscarPorId(idUsuario);
        if (usuario == null) {
            SESIONES.remove(token);
            return null;
        }

        return usuario;
    }

    /**
     * Extrae el token de sesion de la cookie primaria o del bearer fallback.
     *
     * @param exchange la peticion HTTP actual.
     * @return token de sesion, o null si no hay ninguno utilizable.
     */
    public String obtenerTokenDeSesion(HttpExchange exchange) {
        String tokenCookie = extraerTokenDeCookie(exchange);
        if (tokenCookie != null) {
            return tokenCookie;
        }

        return extraerBearerToken(exchange);
    }

    private String extraerTokenDeCookie(HttpExchange exchange) {
        List<String> cookieHeaders = exchange.getRequestHeaders().get("Cookie");
        if (cookieHeaders == null || cookieHeaders.isEmpty()) {
            return null;
        }

        for (String cookieHeader : cookieHeaders) {
            if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
                continue;
            }

            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String cookieNormalizada = cookie.trim();
                int separador = cookieNormalizada.indexOf('=');
                if (separador <= 0) {
                    continue;
                }

                String nombre = cookieNormalizada.substring(0, separador).trim();
                if (!SESSION_COOKIE_NAME.equals(nombre)) {
                    continue;
                }

                String valor = cookieNormalizada.substring(separador + 1).trim();
                if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
                    valor = valor.substring(1, valor.length() - 1).trim();
                }

                return valor.isEmpty() ? null : valor;
            }
        }

        return null;
    }

    private String extraerBearerToken(HttpExchange exchange) {
        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
        if (authorization == null) {
            return null;
        }

        String valor = authorization.trim();
        String prefijo = "Bearer ";
        if (!valor.regionMatches(true, 0, prefijo, 0, prefijo.length())) {
            return null;
        }

        String token = valor.substring(prefijo.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
