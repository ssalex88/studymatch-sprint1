package net.studymatch.api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de configuracion encargada de proporcionar conexiones JDBC
 * a la base de datos MySQL "studymatch_db" usando JDBC nativo,
 * sin pools de conexiones externos ni frameworks.
 */
public class DatabaseConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/studymatch_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    static {
        try {
            // Carga explicita del driver JDBC de MySQL.
            // En JDBC 4.0+ (Java 6+) no es estrictamente necesario si el driver
            // esta en el classpath (autodescubrimiento via ServiceLoader),
            // pero se deja explicito para mayor claridad y compatibilidad.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontro el driver JDBC de MySQL en el classpath.", e);
        }
    }

    private DatabaseConfig() {
        // Clase utilitaria: se evita la instanciacion.
    }

    /**
     * Abre y retorna una nueva conexion JDBC hacia la base de datos studymatch_db.
     * El llamador es responsable de cerrar la conexion (idealmente con try-with-resources).
     *
     * @return una conexion JDBC activa hacia MySQL.
     * @throws SQLException si ocurre un error al establecer la conexion.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
