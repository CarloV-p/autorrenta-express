package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.List;
import pe.edu.utp.autorenta.domain.model.AlertaMantenimiento;

// interfaz que necesita el Observer y el Area de Taller (ISP)
public interface AlertaMantenimientoDAO {
    AlertaMantenimiento registrar(AlertaMantenimiento alerta);
    List<AlertaMantenimiento> listarPendientes();
    List<AlertaMantenimiento> listarPorVehiculo(String placa);
    void marcarAtendida(int idAlerta);
}
