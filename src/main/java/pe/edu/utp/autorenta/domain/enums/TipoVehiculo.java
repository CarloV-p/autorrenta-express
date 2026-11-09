package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// tipos de vehiculo de la flota 
// Factory Method crea un contrato según este valor (modulo 2)
public enum TipoVehiculo {
    ECONOMICO("Económico"),
    SUV_4X4("SUV 4x4"),
    CAMIONETA_CARGA("Camioneta de Carga"),
    ELECTRICO("Vehículo Eléctrico");

    private final String etiqueta;

    TipoVehiculo(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
