package net.studymatch.api.dto;

/**
 * DTO utilizado para enviar de forma segura la informacion del usuario
 * autenticado hacia el frontend (React), sin exponer la contrasena.
 */
public class AuthResponseDTO {

    private int idUsuario;
    private String nombreCompleto;
    private String correoInstitucional;
    private String rol;
    private String carrera;
    private String ciclo;
    private String codigoAlumno;
    private String biografia;

    /**
     * Constructor vacio, util para instanciacion generica o deserializacion.
     */
    public AuthResponseDTO() {
    }

    /**
     * Constructor completo, pensado para construir la respuesta
     * rapidamente a partir de los datos de un Usuario autenticado.
     */
    public AuthResponseDTO(int idUsuario, String nombreCompleto, String correoInstitucional,
                           String rol, String carrera, String ciclo,
                           String codigoAlumno, String biografia) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoInstitucional = correoInstitucional;
        this.rol = rol;
        this.carrera = carrera;
        this.ciclo = ciclo;
        this.codigoAlumno = codigoAlumno;
        this.biografia = biografia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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

    public String getCodigoAlumno() {
        return codigoAlumno;
    }

    public void setCodigoAlumno(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
}
