package net.studymatch.api.entity;

/**
 * Entidad que representa la tabla "usuarios" de la base de datos studymatch_db.
 * Mapea directamente las columnas: id_usuario, nombre_completo,
 * correo_institucional, contrasena, rol, carrera, ciclo, codigo_alumno
 * y biografia.
 */
public class Usuario {

    private int idUsuario;
    private String nombreCompleto;
    private String correoInstitucional;
    private String contrasena;
    private String rol;
    private String carrera;
    private String ciclo;
    private String codigoAlumno;
    private String biografia;

    /**
     * Constructor vacio, requerido para instanciacion generica
     * (por ejemplo, al mapear filas de un ResultSet).
     */
    public Usuario() {
    }

    /**
     * Constructor completo (version original) con los atributos base
     * de la entidad, sin codigoAlumno ni biografia. Se conserva por
     * compatibilidad con el codigo existente.
     */
    public Usuario(int idUsuario, String nombreCompleto, String correoInstitucional,
                   String contrasena, String rol, String carrera, String ciclo) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoInstitucional = correoInstitucional;
        this.contrasena = contrasena;
        this.rol = rol;
        this.carrera = carrera;
        this.ciclo = ciclo;
    }

    /**
     * Constructor completo (version extendida) incluyendo codigoAlumno
     * y biografia.
     */
    public Usuario(int idUsuario, String nombreCompleto, String correoInstitucional,
                   String contrasena, String rol, String carrera, String ciclo,
                   String codigoAlumno, String biografia) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoInstitucional = correoInstitucional;
        this.contrasena = contrasena;
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
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
