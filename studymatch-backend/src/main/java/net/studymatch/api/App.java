package net.studymatch.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import net.studymatch.api.controller.UsuarioController;

/**
 * Punto de entrada principal de la API REST de StudyMatch. Inicializa un
 * HttpServer nativo (com.sun.net.httpserver) en el puerto 8080, usando un pool
 * de hilos fijo para atender las peticiones de forma concurrente, y registra el
 * contexto base "/api/" que es atendido por AuthController.
 */
public class App {

    private static final int PUERTO = 8080;
    private static final int TAMANO_POOL_HILOS = 10;

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);

            ExecutorService pool = Executors.newFixedThreadPool(TAMANO_POOL_HILOS);
            server.setExecutor(pool);

            // Registro del contexto base de la API. Todas las rutas que
            // comiencen con /api/ (por ejemplo /api/auth/registrar,
            // /api/auth/login, /api/usuarios) son manejadas por AuthController.

            server.createContext("/api/usuarios", new UsuarioController());
            server.start();

            System.out.println("========================================");
            System.out.println(" StudyMatch API iniciada correctamente");
            System.out.println(" Servidor escuchando en el puerto: " + PUERTO);
            System.out.println(" URL base: http://localhost:" + PUERTO);
            System.out.println(" Contexto registrado: /api/ -> AuthController");
            System.out.println("========================================");

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor HTTP en el puerto " + PUERTO + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
