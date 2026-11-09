package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// roles del sistema
public enum RolUsuario {
    ADMINISTRADOR("Administrador"),
    AGENTE("Agente de Alquiler");

    private final String etiqueta;

    RolUsuario(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
