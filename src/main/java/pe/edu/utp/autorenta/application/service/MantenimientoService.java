package pe.edu.utp.autorenta.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoMantenimiento;
import pe.edu.utp.autorenta.domain.enums.TipoMantenimiento;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.AlertaMantenimiento;
import pe.edu.utp.autorenta.domain.model.Mantenimiento;
import pe.edu.utp.autorenta.domain.model.PoliticaDeposito;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.patrones.comportamiento.observer.AlertaMantenimientoObserver;
import pe.edu.utp.autorenta.patrones.comportamiento.observer.ObservadorMantenimiento;
import pe.edu.utp.autorenta.patrones.comportamiento.observer.SujetoFlota;
import pe.edu.utp.autorenta.persistence.dao.AlertaMantenimientoDAO;
import pe.edu.utp.autorenta.persistence.dao.MantenimientoDAO;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.AlertaMantenimientoDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.MantenimientoDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.VehiculoDAOImpl;

// Patrón GoF: ConcreteSubject del patrón Observer (implementa SujetoFlota y notifica alertas de km)
// Principios SOLID: SRP (gestión de mantenimientos y monitoreo de km), OCP y DIP (inyección de interfaces DAO)
// GRASP: Experto en Información, Bajo Acoplamiento y Alta Cohesión
// Autor: Chamorro Casani Oscar Gonzalo
public class MantenimientoService implements SujetoFlota {

    public static final double INTERVALO_KM_PREVENTIVO_POR_DEFECTO = 5000.0;

    private final MantenimientoDAO mantenimientoDAO;
    private final VehiculoDAO vehiculoDAO;
    private final AlertaMantenimientoDAO alertaMantenimientoDAO;
    private final PoliticaDeposito politicaDeposito;
    private final List<ObservadorMantenimiento> observadores;

    public MantenimientoService() {
        this(new ConexionBD());
    }

    public MantenimientoService(ConexionBD conexion) {
        this(
                new MantenimientoDAOImpl(conexion),
                new VehiculoDAOImpl(conexion),
                new AlertaMantenimientoDAOImpl(conexion),
                new PoliticaDeposito(0.20, 200.0, INTERVALO_KM_PREVENTIVO_POR_DEFECTO)
        );
    }

    public MantenimientoService(MantenimientoDAO mantenimientoDAO,
                                VehiculoDAO vehiculoDAO,
                                AlertaMantenimientoDAO alertaMantenimientoDAO) {
        this(
                mantenimientoDAO,
                vehiculoDAO,
                alertaMantenimientoDAO,
                new PoliticaDeposito(0.20, 200.0, INTERVALO_KM_PREVENTIVO_POR_DEFECTO)
        );
    }

    // Constructor con Inyección de Dependencias (DIP)
    public MantenimientoService(MantenimientoDAO mantenimientoDAO,
                                VehiculoDAO vehiculoDAO,
                                AlertaMantenimientoDAO alertaMantenimientoDAO,
                                PoliticaDeposito politicaDeposito) {
        if (mantenimientoDAO == null || vehiculoDAO == null || alertaMantenimientoDAO == null || politicaDeposito == null) {
            throw new DatosInvalidosException("Las dependencias de MantenimientoService no pueden ser nulas.");
        }
        this.mantenimientoDAO = mantenimientoDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.alertaMantenimientoDAO = alertaMantenimientoDAO;
        this.politicaDeposito = politicaDeposito;
        this.observadores = new ArrayList<>();
        this.observadores.add(new AlertaMantenimientoObserver(alertaMantenimientoDAO));
    }

    @Override
    public final void agregarObservador(ObservadorMantenimiento observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void eliminarObservador(ObservadorMantenimiento observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(Vehiculo vehiculo) {
        for (ObservadorMantenimiento observador : observadores) {
            observador.actualizar(vehiculo);
        }
    }

    // Actualiza el kilometraje en BD y dispara los observadores si supera el límite preventivo
    public boolean actualizarKilometrajeYVerificar(String placa, double nuevoKilometraje) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("La placa del vehículo es obligatoria.");
        }
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(placa.trim().toUpperCase())
                .orElseThrow(() -> new DatosInvalidosException("No se encontró el vehículo con placa: " + placa));

        boolean requiereRevision = vehiculo.registrarKilometraje(nuevoKilometraje);
        vehiculoDAO.actualizar(vehiculo);

        if (requiereRevision) {
            notificarObservadores(vehiculo);
        }
        return requiereRevision;
    }

    public boolean verificarYNotificarMantenimiento(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo a evaluar no puede ser nulo.");
        }
        if (vehiculo.requiereMantenimiento()) {
            notificarObservadores(vehiculo);
            return true;
        }
        return false;
    }

    public Mantenimiento registrarMantenimiento(Mantenimiento mantenimiento) {
        if (mantenimiento == null) {
            throw new DatosInvalidosException("Los datos del mantenimiento son obligatorios.");
        }
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(mantenimiento.getPlaca())
                .orElseThrow(() -> new DatosInvalidosException("No existe el vehículo con placa: " + mantenimiento.getPlaca()));

        Mantenimiento guardado = mantenimientoDAO.guardar(mantenimiento);
        if (mantenimiento.getEstado() != EstadoMantenimiento.FINALIZADO) {
            vehiculo.enviarAMantenimiento();
            vehiculoDAO.actualizar(vehiculo);
        }
        return guardado;
    }

    public Mantenimiento registrarMantenimiento(String placa, LocalDate fecha, TipoMantenimiento tipo,
                                                double costo, String descripcion) {
        Mantenimiento nuevo = new Mantenimiento(placa, fecha, tipo, EstadoMantenimiento.EN_PROCESO, costo, descripcion);
        return registrarMantenimiento(nuevo);
    }

    // Finaliza el mantenimiento, devuelve el vehículo a DISPONIBLE y atiende las alertas pendientes
    public void finalizarMantenimiento(int idMantenimiento, double costoFinal, String descripcionActualizada) {
        Mantenimiento mantenimiento = mantenimientoDAO.buscarPorId(idMantenimiento)
                .orElseThrow(() -> new DatosInvalidosException("No existe la orden de mantenimiento #" + idMantenimiento));

        mantenimiento.setEstado(EstadoMantenimiento.FINALIZADO);
        mantenimiento.setCosto(costoFinal);
        if (descripcionActualizada != null && !descripcionActualizada.isBlank()) {
            mantenimiento.setDescripcion(descripcionActualizada);
        }
        mantenimientoDAO.actualizar(mantenimiento);

        Vehiculo vehiculo = vehiculoDAO.buscarPorId(mantenimiento.getPlaca())
                .orElseThrow(() -> new DatosInvalidosException("No existe el vehículo asociado: " + mantenimiento.getPlaca()));

        vehiculo.marcarDisponible();
        if (vehiculo.getKilometraje() >= vehiculo.getKilometrajeProximoMantenimiento()
                || mantenimiento.getTipo() == TipoMantenimiento.PREVENTIVO) {
            vehiculo.programarSiguienteMantenimiento(politicaDeposito.getIntervaloKmMantenimiento());
        }
        vehiculoDAO.actualizar(vehiculo);

        List<AlertaMantenimiento> alertasVehiculo = alertaMantenimientoDAO.listarPorVehiculo(vehiculo.getPlaca());
        for (AlertaMantenimiento alerta : alertasVehiculo) {
            if (!alerta.isAtendida()) {
                alertaMantenimientoDAO.marcarAtendida(alerta.getIdAlerta());
            }
        }
    }

    public void finalizarMantenimiento(int idMantenimiento) {
        Mantenimiento mantenimiento = mantenimientoDAO.buscarPorId(idMantenimiento)
                .orElseThrow(() -> new DatosInvalidosException("No existe la orden de mantenimiento #" + idMantenimiento));
        finalizarMantenimiento(idMantenimiento, mantenimiento.getCosto(), mantenimiento.getDescripcion());
    }

    public List<Mantenimiento> listarTodos() {
        return mantenimientoDAO.listarTodos();
    }

    public List<Mantenimiento> listarPorVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("La placa es obligatoria para consultar el historial.");
        }
        return mantenimientoDAO.listarPorVehiculo(placa.trim().toUpperCase());
    }

    public Optional<Mantenimiento> buscarPorId(int idMantenimiento) {
        return mantenimientoDAO.buscarPorId(idMantenimiento);
    }

    public List<AlertaMantenimiento> listarAlertasPendientes() {
        return alertaMantenimientoDAO.listarPendientes();
    }

    public List<AlertaMantenimiento> listarAlertasPorVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("La placa es obligatoria para consultar las alertas.");
        }
        return alertaMantenimientoDAO.listarPorVehiculo(placa.trim().toUpperCase());
    }

    public void marcarAlertaAtendida(int idAlerta) {
        alertaMantenimientoDAO.marcarAtendida(idAlerta);
    }
}