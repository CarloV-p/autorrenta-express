package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// estados por los que pasa un contrato de alquiler
public enum EstadoContrato {
    ACTIVO("Activo"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoContrato(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
