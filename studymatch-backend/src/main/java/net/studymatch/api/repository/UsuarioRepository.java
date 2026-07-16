package net.studymatch.api.repository;

import net.studymatch.api.config.DatabaseConfig;
import net.studymatch.api.entity.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de persistencia para la entidad Usuario.
 * Usa JDBC nativo (PreparedStatement y ResultSet) sobre MySQL,
 * obteniendo las conexiones a traves de DatabaseConfig.getConnection().
 */
public class UsuarioRepository {

    private static final String SQL_INSERT =
            "INSERT INTO usuarios (nombre_completo, correo_institucional, contrasena, rol, carrera, ciclo) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_POR_CORREO =
            "SELECT * FROM usuarios WHERE correo_institucional = ?";

    private static final String SQL_SELECT_TODOS =
            "SELECT id_usuario, nombre_completo, correo_institucional, rol, carrera, ciclo, " +
            "codigo_alumno, biografia FROM usuarios";

    private static final String SQL_SELECT_POR_ID =
            "SELECT id_usuario, nombre_completo, correo_institucional, rol, carrera, ciclo, " +
            "codigo_alumno, biografia FROM usuarios WHERE id_usuario = ?";

    private static final String SQL_UPDATE_PERFIL =
            "UPDATE usuarios SET carrera = ?, ciclo = ?, codigo_alumno = ?, biografia = ? " +
            "WHERE id_usuario = ?";

    private static final String SQL_UPDATE_ROL =
            "UPDATE usuarios SET rol = ? WHERE id_usuario = ?";

    /**
     * Inserta un nuevo usuario en la tabla usuarios.
     *
     * @param usuario el objeto Usuario con los datos a persistir.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public void guardar(Usuario usuario) throws SQLException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_INSERT)) {

            statement.setString(1, usuario.getNombreCompleto());
            statement.setString(2, usuario.getCorreoInstitucional());
            statement.setString(3, usuario.getContrasena());
            statement.setString(4, usuario.getRol());
            statement.setString(5, usuario.getCarrera());
            statement.setString(6, usuario.getCiclo());

            statement.executeUpdate();
        }
    }

    /**
     * Busca un usuario por su correo institucional.
     *
     * @param correo el correo institucional a buscar.
     * @return el Usuario encontrado, o null si no existe ningun registro con ese correo.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public Usuario buscarPorCorreo(String correo) throws SQLException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_SELECT_POR_CORREO)) {

            statement.setString(1, correo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearUsuario(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Recupera todos los usuarios registrados en la base de datos.
     *
     * @return una lista con todos los usuarios encontrados (vacia si no hay ninguno).
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_SELECT_TODOS);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(mapearUsuarioSinContrasena(resultSet));
            }
        }

        return usuarios;
    }

    /**
     * Busca un usuario por su identificador sin incluir la contrasena.
     *
     * @param idUsuario el identificador del usuario a buscar.
     * @return el Usuario encontrado sin contrasena, o null si no existe.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public Usuario buscarPorId(int idUsuario) throws SQLException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_SELECT_POR_ID)) {

            statement.setInt(1, idUsuario);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearUsuarioSinContrasena(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Actualiza los datos de perfil academico de un usuario: carrera, ciclo,
     * codigo de alumno y biografia. No modifica correo, contrasena ni rol.
     *
     * @param idUsuario    el identificador del usuario a actualizar.
     * @param carrera      la nueva carrera del usuario.
     * @param ciclo        el nuevo ciclo del usuario.
     * @param codigoAlumno el nuevo codigo de alumno del usuario.
     * @param biografia    la nueva biografia del usuario.
     * @return true si se actualizo exactamente un registro, false en caso contrario.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public boolean actualizarPerfil(int idUsuario, String carrera, String ciclo,
                                     String codigoAlumno, String biografia) throws SQLException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_UPDATE_PERFIL)) {

            statement.setString(1, carrera);
            statement.setString(2, ciclo);
            statement.setString(3, codigoAlumno);
            statement.setString(4, biografia);
            statement.setInt(5, idUsuario);

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas == 1;
        }
    }

    /**
     * Actualiza el rol de un usuario (por ejemplo, de "Estudiante" a "Administrador").
     *
     * @param idUsuario el identificador del usuario a actualizar.
     * @param nuevoRol  el nuevo rol a asignar.
     * @return true si se actualizo exactamente un registro, false en caso contrario.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public boolean actualizarRol(int idUsuario, String nuevoRol) throws SQLException {
        try (Connection conexion = DatabaseConfig.getConnection();
             PreparedStatement statement = conexion.prepareStatement(SQL_UPDATE_ROL)) {

            statement.setString(1, nuevoRol);
            statement.setInt(2, idUsuario);

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas == 1;
        }
    }

    /**
     * Mapea la fila actual de un ResultSet a un objeto Usuario.
     *
     * @param resultSet el ResultSet posicionado en la fila a mapear.
     * @return el objeto Usuario construido a partir de la fila actual.
     * @throws SQLException si ocurre un error al leer alguna columna.
     */
    private Usuario mapearUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultSet.getInt("id_usuario"));
        usuario.setNombreCompleto(resultSet.getString("nombre_completo"));
        usuario.setCorreoInstitucional(resultSet.getString("correo_institucional"));
        usuario.setContrasena(resultSet.getString("contrasena"));
        usuario.setRol(resultSet.getString("rol"));
        usuario.setCarrera(resultSet.getString("carrera"));
        usuario.setCiclo(resultSet.getString("ciclo"));
        usuario.setCodigoAlumno(resultSet.getString("codigo_alumno"));
        usuario.setBiografia(resultSet.getString("biografia"));
        return usuario;
    }

    /**
     * Mapea la fila actual de un ResultSet a un objeto Usuario, sin leer la
     * columna "contrasena". Se usa en consultas (como listarTodos) cuyo SQL
     * no selecciona esa columna por motivos de seguridad.
     *
     * @param resultSet el ResultSet posicionado en la fila a mapear.
     * @return el objeto Usuario construido a partir de la fila actual (sin contrasena).
     * @throws SQLException si ocurre un error al leer alguna columna.
     */
    private Usuario mapearUsuarioSinContrasena(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultSet.getInt("id_usuario"));
        usuario.setNombreCompleto(resultSet.getString("nombre_completo"));
        usuario.setCorreoInstitucional(resultSet.getString("correo_institucional"));
        usuario.setRol(resultSet.getString("rol"));
        usuario.setCarrera(resultSet.getString("carrera"));
        usuario.setCiclo(resultSet.getString("ciclo"));
        usuario.setCodigoAlumno(resultSet.getString("codigo_alumno"));
        usuario.setBiografia(resultSet.getString("biografia"));
        return usuario;
    }
}
