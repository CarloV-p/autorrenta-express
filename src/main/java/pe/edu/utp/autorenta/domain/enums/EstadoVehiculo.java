package pe.edu.utp.autorenta.domain.enums;
// @author Valdivia Paricahua Carlo Alexander

// estado operativo de un vehiculo dentro de la flota
public enum EstadoVehiculo {
    DISPONIBLE("Disponible"),
    ALQUILADO("Alquilado"),
    EN_MANTENIMIENTO("En mantenimiento");

    private final String etiqueta;

    EstadoVehiculo(String etiqueta){this.etiqueta = etiqueta;}

    public String getEtiqueta(){return etiqueta;}

    @Override
    public String toString(){return etiqueta;}
}
