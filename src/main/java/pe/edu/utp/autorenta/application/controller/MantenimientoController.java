package pe.edu.utp.autorenta.application.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.application.service.MantenimientoService;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.TipoMantenimiento;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.AlertaMantenimiento;
import pe.edu.utp.autorenta.domain.model.Mantenimiento;
import pe.edu.utp.autorenta.patrones.comportamiento.observer.ObservadorMantenimiento;
import pe.edu.utp.autorenta.persistence.dao.AlertaMantenimientoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.AlertaMantenimientoDAOImpl;

// Patrón GRASP: Controlador (Coordina el módulo de mantenimientos y alertas para la vista Swing)
// Principios SOLID: SRP y DIP (recibe MantenimientoService y AlertaMantenimientoDAO)
// Autor: Chamorro Casani Oscar Gonzalo
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;
    private final AlertaMantenimientoDAO alertaMantenimientoDAO;

    public MantenimientoController() {
        ConexionBD conexion = new ConexionBD();
        conexion.inicializarEsquema();
        this.alertaMantenimientoDAO = new AlertaMantenimientoDAOImpl(conexion);
        this.mantenimientoService = new MantenimientoService(conexion);
    }

    public MantenimientoController(MantenimientoService mantenimientoService,
                                   AlertaMantenimientoDAO alertaMantenimientoDAO) {
        if (mantenimientoService == null || alertaMantenimientoDAO == null) {
            throw new DatosInvalidosException("MantenimientoService y AlertaMantenimientoDAO no pueden ser nulos.");
        }
        this.mantenimientoService = mantenimientoService;
        this.alertaMantenimientoDAO = alertaMantenimientoDAO;
    }

    public Mantenimiento registrarMantenimiento(Mantenimiento mantenimiento) {
        return mantenimientoService.registrarMantenimiento(mantenimiento);
    }

    public Mantenimiento registrarMantenimiento(String placa, LocalDate fecha, TipoMantenimiento tipo,
                                                double costo, String descripcion) {
        return mantenimientoService.registrarMantenimiento(placa, fecha, tipo, costo, descripcion);
    }

    public void finalizarMantenimiento(int idMantenimiento, double costoFinal, String descripcionActualizada) {
        mantenimientoService.finalizarMantenimiento(idMantenimiento, costoFinal, descripcionActualizada);
    }

    public void finalizarMantenimiento(int idMantenimiento) {
        mantenimientoService.finalizarMantenimiento(idMantenimiento);
    }

    public List<Mantenimiento> listarHistorialMantenimientos() {
        return mantenimientoService.listarTodos();
    }

    public List<Mantenimiento> listarMantenimientosPorVehiculo(String placa) {
        return mantenimientoService.listarPorVehiculo(placa);
    }

    public Optional<Mantenimiento> buscarMantenimientoPorId(int idMantenimiento) {
        return mantenimientoService.buscarPorId(idMantenimiento);
    }

    public List<AlertaMantenimiento> listarAlertasActivas() {
        return alertaMantenimientoDAO.listarPendientes();
    }

    public List<AlertaMantenimiento> listarAlertasPorVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("Debe indicar una placa válida.");
        }
        return alertaMantenimientoDAO.listarPorVehiculo(placa.trim().toUpperCase());
    }

    public void atenderAlerta(int idAlerta) {
        alertaMantenimientoDAO.marcarAtendida(idAlerta);
    }

    public void agregarObservadorMantenimiento(ObservadorMantenimiento observador) {
        mantenimientoService.agregarObservador(observador);
    }

    public MantenimientoService getMantenimientoService() {
        return mantenimientoService;
    }
}