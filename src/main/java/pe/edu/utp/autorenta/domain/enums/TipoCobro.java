package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

/* 
    algoritmo de cobro elegido para el contrato (Strategy) (modulo 6)
    se persiste para saber qué estrategia reconstruir
*/
public enum TipoCobro {
    POR_DIA("Tarifa por Día"),
    KILOMETRAJE_LIBRE("Kilometraje Libre"),
    MENSUAL_CORPORATIVO("Alquiler Mensual Corporativo");

    private final String etiqueta;

    TipoCobro(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
