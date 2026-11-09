package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// distingue contrato simple de corporativo (se guarda en la misma tabla)
public enum TipoContrato {
    SIMPLE("Simple"),
    CORPORATIVO("Corporativo");

    private final String etiqueta;

    TipoContrato(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
