package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.List;
import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.domain.enums.TipoVehiculo;
import pe.edu.utp.autorenta.domain.model.Vehiculo;

// la clave primaria del vehiculo es su placa (String)
public interface VehiculoDAO extends GenericDAO<Vehiculo, String> {
    List<Vehiculo> listarPorEstado(EstadoVehiculo estado);
    List<Vehiculo> listarPorTipo(TipoVehiculo tipo);
    
    // vehiculos DISPONIBLES que no tienen contrato ACTIVO que se cruce con el rango dado
    List<Vehiculo> listarDisponiblesEntre(java.time.LocalDate inicio, java.time.LocalDate fin);
}
