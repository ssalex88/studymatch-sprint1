package net.studymatch.api.service;

import net.studymatch.api.dto.AuthResponseDTO;
import net.studymatch.api.dto.LoginRequestDTO;
import net.studymatch.api.dto.UsuarioRegistroDTO;
import net.studymatch.api.entity.Usuario;
import net.studymatch.api.repository.UsuarioRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de logica de negocio para todo lo relacionado con la autenticacion
 * y gestion de usuarios de StudyMatch: registro, inicio de sesion y listado.
 */
public class UsuarioService {

    private UsuarioRepository usuarioRepository = new UsuarioRepository();

    private static final String ROL_POR_DEFECTO = "Estudiante";

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida que el correo institucional no este previamente registrado,
     * cifra la contrasena con SHA-256, asigna un rol por defecto si no viene
     * especificado, persiste el usuario y retorna sus datos seguros.
     *
     * @param dto los datos de registro capturados desde el frontend.
     * @return un AuthResponseDTO con la informacion segura del usuario creado.
     * @throws Exception si ocurre un error de validacion, de cifrado o de base de datos.
     */
    public AuthResponseDTO registrarUsuario(UsuarioRegistroDTO dto) throws Exception {
        Usuario usuarioExistente = usuarioRepository.buscarPorCorreo(dto.getCorreoInstitucional());
        if (usuarioExistente != null) {
            throw new IllegalArgumentException("El correo institucional ya se encuentra registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(dto.getNombreCompleto());
        usuario.setCorreoInstitucional(dto.getCorreoInstitucional());
        usuario.setContrasena(cifrarSha256(dto.getContrasena()));
        usuario.setCarrera(dto.getCarrera());
        usuario.setCiclo(dto.getCiclo());
        usuario.setRol(ROL_POR_DEFECTO);

        usuarioRepository.guardar(usuario);

        Usuario usuarioCreado = usuarioRepository.buscarPorCorreo(usuario.getCorreoInstitucional());

        return mapearAAuthResponseDTO(usuarioCreado);
    }

    /**
     * Autentica a un usuario validando su correo institucional y contrasena.
     *
     * @param dto las credenciales de acceso capturadas desde el frontend.
     * @return un AuthResponseDTO con la informacion segura del usuario autenticado.
     * @throws Exception si las credenciales son invalidas o ocurre un error de base de datos.
     */
    public AuthResponseDTO iniciarSesion(LoginRequestDTO dto) throws Exception {
        Usuario usuario = usuarioRepository.buscarPorCorreo(dto.getCorreoInstitucional());
        if (usuario == null) {
            throw new SecurityException("Credenciales de acceso incorrectas.");
        }

        String contrasenaCifrada = cifrarSha256(dto.getContrasena());
        if (!contrasenaCifrada.equals(usuario.getContrasena())) {
            throw new SecurityException("Credenciales de acceso incorrectas.");
        }

        return mapearAAuthResponseDTO(usuario);
    }

    /**
     * Recupera todos los usuarios registrados y los mapea a su representacion segura.
     *
     * @return una lista de AuthResponseDTO con la informacion segura de cada usuario.
     * @throws Exception si ocurre un error al consultar la base de datos.
     */
    public List<AuthResponseDTO> listarTodosLosUsuarios() throws Exception {
        List<Usuario> usuarios = usuarioRepository.listarTodos();
        List<AuthResponseDTO> respuesta = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            respuesta.add(mapearAAuthResponseDTO(usuario));
        }

        return respuesta;
    }

    /**
     * Actualiza los datos de perfil academico de un usuario (carrera, ciclo,
     * codigo de alumno y biografia). Metodo puente directo hacia
     * UsuarioRepository.actualizarPerfil.
     *
     * @param idUsuario    el identificador del usuario a actualizar.
     * @param carrera      la nueva carrera del usuario.
     * @param ciclo        el nuevo ciclo del usuario.
     * @param codigoAlumno el nuevo codigo de alumno del usuario.
     * @param biografia    la nueva biografia del usuario.
     * @return true si el perfil se actualizo correctamente, false en caso contrario.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public boolean modificarPerfil(int idUsuario, String carrera, String ciclo,
                                    String codigoAlumno, String biografia) throws SQLException {
        return usuarioRepository.actualizarPerfil(idUsuario, carrera, ciclo, codigoAlumno, biografia);
    }

    /**
     * Recupera la lista completa de usuarios registrados en el sistema.
     * Metodo puente directo hacia UsuarioRepository.listarTodos.
     *
     * @return una lista con todos los usuarios (sin contrasena).
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public List<Usuario> obtenerTodosLosUsuarios() throws SQLException {
        return usuarioRepository.listarTodos();
    }

    /**
     * Cambia el rol asignado a un usuario (por ejemplo, de "Estudiante" a
     * "Administrador"). Metodo puente directo hacia UsuarioRepository.actualizarRol.
     *
     * @param idUsuario el identificador del usuario a actualizar.
     * @param nuevoRol  el nuevo rol a asignar.
     * @return true si el rol se actualizo correctamente, false en caso contrario.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public boolean cambiarRolUsuario(int idUsuario, String nuevoRol) throws SQLException {
        return usuarioRepository.actualizarRol(idUsuario, nuevoRol);
    }

    /**
     * Convierte una entidad Usuario en su representacion segura AuthResponseDTO,
     * excluyendo la contrasena.
     *
     * @param usuario la entidad Usuario a mapear.
     * @return el AuthResponseDTO correspondiente.
     */
    private AuthResponseDTO mapearAAuthResponseDTO(Usuario usuario) {
        return new AuthResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombreCompleto(),
                usuario.getCorreoInstitucional(),
                usuario.getRol(),
                usuario.getCarrera(),
                usuario.getCiclo(),
                usuario.getCodigoAlumno(),
                usuario.getBiografia()
        );
    }

    /**
     * Cifra un texto plano utilizando el algoritmo SHA-256 nativo de Java,
     * devolviendo el resultado como una cadena hexadecimal en minusculas.
     *
     * @param textoPlano el texto a cifrar (por ejemplo, una contrasena).
     * @return el hash SHA-256 en formato hexadecimal.
     * @throws NoSuchAlgorithmException si el algoritmo SHA-256 no esta disponible en el entorno.
     */
    private String cifrarSha256(String textoPlano) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(textoPlano.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexBuilder.append('0');
            }
            hexBuilder.append(hex);
        }

        return hexBuilder.toString();
    }
}
