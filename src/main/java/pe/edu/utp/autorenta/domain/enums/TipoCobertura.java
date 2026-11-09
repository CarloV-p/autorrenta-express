package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// coberturas y extras disponibles (Decorator) (modulo 4)
public enum TipoCobertura {
    TODO_RIESGO("Seguro Todo Riesgo"),
    GPS("Dispositivo GPS"),
    SILLA_BEBE("Silla de Bebé"),
    CONDUCTOR_ADICIONAL("Conductor Adicional");

    private final String etiqueta;

    TipoCobertura(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
