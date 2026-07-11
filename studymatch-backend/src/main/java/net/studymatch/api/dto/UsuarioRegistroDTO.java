package net.studymatch.api.dto;

/**
 * DTO utilizado para capturar el JSON enviado desde el frontend
 * en el formulario de registro de un nuevo usuario.
 * No incluye idUsuario ni rol, ya que estos son asignados por el backend.
 */
public class UsuarioRegistroDTO {

    private String nombreCompleto;
    private String correoInstitucional;
    private String contrasena;
    private String carrera;
    private String ciclo;

    /**
     * Constructor vacio, requerido por Gson para deserializar el JSON entrante.
     */
    public UsuarioRegistroDTO() {
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
}
