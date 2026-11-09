package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// estados de una orden de mantenimiento
public enum EstadoMantenimiento {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    FINALIZADO("Finalizado");

    private final String etiqueta;

    EstadoMantenimiento(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
