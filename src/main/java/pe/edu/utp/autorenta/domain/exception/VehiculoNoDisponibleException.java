package pe.edu.utp.autorenta.domain.exception;
// @author Valdivia Paricahua Carlo Alexander

// se lanza al intentar alquilar un vehiculo que no esta disponible
public class VehiculoNoDisponibleException extends AutoRentaException {
    private final String placa;

    public VehiculoNoDisponibleException(String placa){
        super("El vehículo con placa " + placa + " no está disponible para alquiler.");
        this.placa = placa;
    }

    public String getPlaca(){return placa;}
}
