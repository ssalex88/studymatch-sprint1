package net.studymatch.api.service;

import com.sun.net.httpserver.HttpExchange;
import net.studymatch.api.entity.Usuario;
import net.studymatch.api.repository.UsuarioRepository;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Servicio simple de sesiones en memoria para Sprint 1.
 *
 * <p>Limitacion intencional: las sesiones se pierden al reiniciar el backend
 * y no son aptas para produccion. Sirven para que la demo academica no dependa
 * solamente de localStorage y pueda validar Authorization: Bearer en el backend.</p>
 */
public class SessionService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder TOKEN_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final ConcurrentMap<String, Integer> SESIONES = new ConcurrentHashMap<>();
    private static final int TOKEN_BYTES = 32;

    private UsuarioRepository usuarioRepository = new UsuarioRepository();

    /**
     * Crea una sesion para el usuario indicado y devuelve el token asociado.
     *
     * @param usuario el usuario autenticado o recien registrado.
     * @return token opaco para enviar como Authorization: Bearer.
     */
    public String crearSesion(Usuario usuario) {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        String token = TOKEN_ENCODER.encodeToString(bytes);

        SESIONES.put(token, usuario.getIdUsuario());
        return token;
    }

    /**
     * Resuelve el usuario actual a partir de Authorization: Bearer <token>.
     *
     * @param exchange la peticion HTTP actual.
     * @return el usuario autenticado sin contrasena, o null si falta o no es valido.
     * @throws SQLException si falla la consulta del usuario vigente.
     */
    public Usuario obtenerUsuarioAutenticado(HttpExchange exchange) throws SQLException {
        String token = extraerBearerToken(exchange);
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
