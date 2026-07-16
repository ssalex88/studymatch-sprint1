package net.studymatch.api.config;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * Clase utilitaria para centralizar el manejo de CORS (Cross-Origin Resource Sharing)
 * en los distintos handlers del HttpServer nativo de StudyMatch.
 */
public class CorsHelper {

    private static final String ALLOW_ORIGIN = "http://localhost:5173";
    private static final String ALLOW_METHODS = "GET, POST, PUT, PATCH, DELETE, OPTIONS";
    private static final String ALLOW_HEADERS = "Content-Type, Authorization";
    private static final String ALLOW_CREDENTIALS = "true";

    private CorsHelper() {
        // Clase utilitaria: se evita la instanciacion.
    }

    /**
     * Inyecta las cabeceras CORS necesarias en la respuesta HTTP.
     * Debe llamarse antes de enviar cualquier respuesta (sendResponseHeaders).
     *
     * @param exchange el intercambio HTTP actual.
     */
    public static void aplicarCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", ALLOW_ORIGIN);
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", ALLOW_METHODS);
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", ALLOW_HEADERS);
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", ALLOW_CREDENTIALS);
    }

    /**
     * Detecta si la peticion entrante es un preflight request (metodo OPTIONS).
     * Si lo es, aplica las cabeceras CORS, responde con HTTP 204 (No Content),
     * cierra el exchange y retorna true para que el handler que invoca este
     * metodo detenga cualquier procesamiento adicional.
     *
     * Si no es OPTIONS, retorna false y no realiza ninguna accion sobre el exchange,
     * dejando que el handler continue su flujo normal.
     *
     * @param exchange el intercambio HTTP actual.
     * @return true si la peticion era un preflight y ya fue respondida; false en caso contrario.
     * @throws IOException si ocurre un error al escribir la respuesta.
     */
    public static boolean manejarPreflight(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            aplicarCors(exchange);
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return true;
        }
        return false;
    }
}
