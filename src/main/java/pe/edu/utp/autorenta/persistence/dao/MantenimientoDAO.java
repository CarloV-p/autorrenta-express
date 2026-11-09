package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.List;
import pe.edu.utp.autorenta.domain.model.Mantenimiento;

public interface MantenimientoDAO extends GenericDAO<Mantenimiento, Integer> {
    List<Mantenimiento> listarPorVehiculo(String placa);
}
