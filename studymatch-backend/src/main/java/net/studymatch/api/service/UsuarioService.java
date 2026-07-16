package net.studymatch.api.service;

import net.studymatch.api.dto.AuthResponseDTO;
import net.studymatch.api.dto.LoginRequestDTO;
import net.studymatch.api.dto.UsuarioRegistroDTO;
import net.studymatch.api.entity.Usuario;
import net.studymatch.api.repository.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Capa de logica de negocio para todo lo relacionado con la autenticacion
 * y gestion de usuarios de StudyMatch: registro, inicio de sesion y listado.
 */
public class UsuarioService {

    private UsuarioRepository usuarioRepository = new UsuarioRepository();
    private SessionService sessionService = new SessionService();

    private static final String ROL_POR_DEFECTO = "Estudiante";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PBKDF2_PREFIX = "PBKDF2";
    private static final int PBKDF2_ITERATIONS = 120_000;
    private static final int PBKDF2_SALT_BYTES = 16;
    private static final int PBKDF2_HASH_BITS = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private static final Pattern HASH_SHA256_LEGACY = Pattern.compile("^[a-fA-F0-9]{64}$");

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida que el correo institucional no este previamente registrado,
     * cifra la contrasena con PBKDF2, asigna un rol por defecto si no viene
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
        usuario.setContrasena(cifrarContrasena(dto.getContrasena()));
        usuario.setCarrera(dto.getCarrera());
        usuario.setCiclo(dto.getCiclo());
        usuario.setRol(ROL_POR_DEFECTO);

        usuarioRepository.guardar(usuario);

        Usuario usuarioCreado = usuarioRepository.buscarPorCorreo(usuario.getCorreoInstitucional());

        AuthResponseDTO respuesta = mapearAAuthResponseDTO(usuarioCreado);
        respuesta.setSessionToken(sessionService.crearSesion(usuarioCreado));
        return respuesta;
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

        String contrasenaAlmacenada = usuario.getContrasena();
        if (!verificarContrasena(dto.getContrasena(), contrasenaAlmacenada)) {
            throw new SecurityException("Credenciales de acceso incorrectas.");
        }

        actualizarHashLegacySiCorresponde(usuario, dto.getContrasena(), contrasenaAlmacenada);

        AuthResponseDTO respuesta = mapearAAuthResponseDTO(usuario);
        respuesta.setSessionToken(sessionService.crearSesion(usuario));
        return respuesta;
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
     * Recupera un usuario por su identificador sin incluir la contrasena.
     * Metodo puente directo hacia UsuarioRepository.buscarPorId.
     *
     * @param idUsuario el identificador del usuario a buscar.
     * @return el usuario encontrado, o null si no existe.
     * @throws SQLException si ocurre un error durante la operacion con la base de datos.
     */
    public Usuario obtenerUsuarioPorId(int idUsuario) throws SQLException {
        return usuarioRepository.buscarPorId(idUsuario);
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
     * Cifra una contrasena usando PBKDF2WithHmacSHA256 con sal aleatoria.
     * El resultado es autocontenido: PBKDF2$iterations$saltBase64$hashBase64.
     *
     * @param textoPlano la contrasena en texto plano recibida desde el login o registro.
     * @return hash persistible en formato PBKDF2 autocontenido.
     * @throws Exception si el algoritmo PBKDF2 no esta disponible.
     */
    private String cifrarContrasena(String textoPlano) throws Exception {
        byte[] salt = new byte[PBKDF2_SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        byte[] hash = calcularPbkdf2(textoPlano, salt, PBKDF2_ITERATIONS, PBKDF2_HASH_BITS);

        return PBKDF2_PREFIX + "$"
                + PBKDF2_ITERATIONS + "$"
                + BASE64_ENCODER.encodeToString(salt) + "$"
                + BASE64_ENCODER.encodeToString(hash);
    }

    private boolean verificarContrasena(String textoPlano, String contrasenaAlmacenada) throws Exception {
        if (contrasenaAlmacenada == null || contrasenaAlmacenada.trim().isEmpty()) {
            return false;
        }

        if (esFormatoPbkdf2(contrasenaAlmacenada)) {
            return verificarPbkdf2(textoPlano, contrasenaAlmacenada);
        }

        if (esHashSha256Legacy(contrasenaAlmacenada)) {
            return verificarSha256Legacy(textoPlano, contrasenaAlmacenada);
        }

        return false;
    }

    private boolean verificarPbkdf2(String textoPlano, String contrasenaAlmacenada) throws Exception {
        String[] partes = contrasenaAlmacenada.split("\\$", -1);
        if (partes.length != 4 || !PBKDF2_PREFIX.equals(partes[0])) {
            return false;
        }

        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = BASE64_DECODER.decode(partes[2]);
            byte[] hashAlmacenado = BASE64_DECODER.decode(partes[3]);

            if (iteraciones <= 0 || salt.length == 0 || hashAlmacenado.length == 0) {
                return false;
            }

            byte[] hashCalculado = calcularPbkdf2(
                    textoPlano,
                    salt,
                    iteraciones,
                    hashAlmacenado.length * 8
            );
            return MessageDigest.isEqual(hashAlmacenado, hashCalculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean verificarSha256Legacy(String textoPlano, String hashAlmacenadoHex) throws Exception {
        byte[] hashCalculado = calcularSha256(textoPlano);
        byte[] hashAlmacenado = convertirHexABytes(hashAlmacenadoHex);
        return MessageDigest.isEqual(hashAlmacenado, hashCalculado);
    }

    private void actualizarHashLegacySiCorresponde(
            Usuario usuario,
            String textoPlano,
            String contrasenaAlmacenada
    ) throws Exception {
        if (esHashSha256Legacy(contrasenaAlmacenada)) {
            usuarioRepository.actualizarContrasena(usuario.getIdUsuario(), cifrarContrasena(textoPlano));
        }
    }

    private boolean esFormatoPbkdf2(String contrasenaAlmacenada) {
        return contrasenaAlmacenada.startsWith(PBKDF2_PREFIX + "$");
    }

    private boolean esHashSha256Legacy(String contrasenaAlmacenada) {
        return HASH_SHA256_LEGACY.matcher(contrasenaAlmacenada).matches();
    }

    private byte[] calcularPbkdf2(String textoPlano, byte[] salt, int iteraciones, int hashBits) throws Exception {
        char[] caracteresContrasena = textoPlano.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(caracteresContrasena, salt, iteraciones, hashBits);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
            Arrays.fill(caracteresContrasena, '\0');
        }
    }

    private byte[] calcularSha256(String textoPlano) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] convertirHexABytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }
}
