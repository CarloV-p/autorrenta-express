package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import pe.edu.utp.autorenta.domain.enums.RolUsuario;

// usuario del sistema
// nunca guarda la contraseña legible, solo su hash
public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String passwordHash;
    private RolUsuario rol;
    private boolean activo;

    public Usuario(String nombreUsuario, String passwordHash, RolUsuario rol, boolean activo){
        this.nombreUsuario = ValidacionDominio.texto(nombreUsuario, "usuario");
        this.passwordHash = ValidacionDominio.texto(passwordHash, "contraseña");
        this.rol = ValidacionDominio.noNulo(rol, "rol");
        this.activo = activo;
    }

    public Usuario(int idUsuario, String nombreUsuario, String passwordHash, RolUsuario rol, boolean activo){
        this(nombreUsuario, passwordHash, rol, activo);
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario(){return idUsuario;}
    public void setIdUsuario(int idUsuario){this.idUsuario = idUsuario;}
    public String getNombreUsuario(){return nombreUsuario;}
    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String h){this.passwordHash = ValidacionDominio.texto(h, "contraseña");}
    public RolUsuario getRol(){return rol;}
    public void setRol(RolUsuario rol){this.rol = ValidacionDominio.noNulo(rol, "rol");}
    public boolean isActivo(){return activo;}
    public void setActivo(boolean activo){this.activo = activo;}
}
