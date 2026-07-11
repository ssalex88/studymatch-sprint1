package net.studymatch.api.dto;

/**
 * DTO utilizado para capturar el JSON enviado desde el frontend
 * en el formulario de inicio de sesion (login).
 */
public class LoginRequestDTO {

    private String correoInstitucional;
    private String contrasena;

    /**
     * Constructor vacio, requerido por Gson para deserializar el JSON entrante.
     */
    public LoginRequestDTO() {
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
