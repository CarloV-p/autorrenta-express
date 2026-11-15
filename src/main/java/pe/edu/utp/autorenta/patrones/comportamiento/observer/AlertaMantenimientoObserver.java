package pe.edu.utp.autorenta.patrones.comportamiento.observer;

import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.AlertaMantenimiento;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.persistence.dao.AlertaMantenimientoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.AlertaMantenimientoDAOImpl;

// Patrón GoF: ConcreteObserver (Reacciona ante exceso de kilometraje guardando la alerta en SQLite)
// Principios SOLID: SRP (solo gestiona la creación de la alerta) y DIP (recibe AlertaMantenimientoDAO por constructor)
// GRASP: Creador, Experto en Información y Bajo Acoplamiento
// Autor: Chamorro Casani Oscar Gonzalo
public class AlertaMantenimientoObserver implements ObservadorMantenimiento {

    private final AlertaMantenimientoDAO alertaMantenimientoDAO;

    // Constructor por defecto con conexión SQLite
    public AlertaMantenimientoObserver() {
        this(new AlertaMantenimientoDAOImpl(new ConexionBD()));
    }

    public AlertaMantenimientoObserver(ConexionBD conexion) {
        this(new AlertaMantenimientoDAOImpl(conexion));
    }

    // Constructor con Inyección de Dependencias (DIP)
    public AlertaMantenimientoObserver(AlertaMantenimientoDAO alertaMantenimientoDAO) {
        if (alertaMantenimientoDAO == null) {
            throw new DatosInvalidosException("El DAO de alertas de mantenimiento no puede ser nulo.");
        }
        this.alertaMantenimientoDAO = alertaMantenimientoDAO;
    }

    @Override
    public void actualizar(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo notificado al observador no puede ser nulo.");
        }
        if (vehiculo.requiereMantenimiento()) {
            AlertaMantenimiento nuevaAlerta = AlertaMantenimiento.para(vehiculo);
            alertaMantenimientoDAO.registrar(nuevaAlerta);
        }
    }

    public AlertaMantenimientoDAO getAlertaMantenimientoDAO() {
        return alertaMantenimientoDAO;
    }
}