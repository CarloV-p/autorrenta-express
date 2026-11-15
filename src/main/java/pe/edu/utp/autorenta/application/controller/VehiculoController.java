package pe.edu.utp.autorenta.application.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.application.service.MantenimientoService;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.domain.enums.TipoVehiculo;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.VehiculoDAOImpl;

// Patrón GRASP: Controlador (Gestiona las peticiones de la UI Swing sobre la flota de vehículos)
// Principios SOLID: SRP y DIP (inyecta VehiculoDAO y MantenimientoService)
// Autor: Chamorro Casani Oscar Gonzalo
public class VehiculoController {

    private final VehiculoDAO vehiculoDAO;
    private final MantenimientoService mantenimientoService;

    public VehiculoController() {
        ConexionBD conexion = new ConexionBD();
        conexion.inicializarEsquema();
        this.vehiculoDAO = new VehiculoDAOImpl(conexion);
        this.mantenimientoService = new MantenimientoService(conexion);
    }

    public VehiculoController(VehiculoDAO vehiculoDAO, MantenimientoService mantenimientoService) {
        if (vehiculoDAO == null || mantenimientoService == null) {
            throw new DatosInvalidosException("VehiculoDAO y MantenimientoService no pueden ser nulos.");
        }
        this.vehiculoDAO = vehiculoDAO;
        this.mantenimientoService = mantenimientoService;
    }

    public Vehiculo registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo a registrar no puede ser nulo.");
        }
        if (vehiculoDAO.buscarPorId(vehiculo.getPlaca()).isPresent()) {
            throw new DatosInvalidosException("Ya existe un vehículo registrado con la placa: " + vehiculo.getPlaca());
        }
        Vehiculo guardado = vehiculoDAO.guardar(vehiculo);
        mantenimientoService.verificarYNotificarMantenimiento(guardado);
        return guardado;
    }

    public Vehiculo registrarVehiculo(String placa, String marca, String modelo, TipoVehiculo tipo,
                                      double kilometraje, double precioBasePorDia, double kmProximoMantenimiento) {
        Vehiculo nuevo = new Vehiculo(placa, marca, modelo, tipo, EstadoVehiculo.DISPONIBLE,
                kilometraje, precioBasePorDia, kmProximoMantenimiento);
        return registrarVehiculo(nuevo);
    }

    public void actualizarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo a actualizar no puede ser nulo.");
        }
        vehiculoDAO.actualizar(vehiculo);
        mantenimientoService.verificarYNotificarMantenimiento(vehiculo);
    }

    public void eliminarVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("Debe proporcionar una placa válida para eliminar.");
        }
        vehiculoDAO.eliminar(placa.trim().toUpperCase());
    }

    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("La placa de búsqueda no puede estar vacía.");
        }
        return vehiculoDAO.buscarPorId(placa.trim().toUpperCase());
    }

    public List<Vehiculo> listarTodos() {
        return vehiculoDAO.listarTodos();
    }

    public List<Vehiculo> listarDisponibles() {
        return vehiculoDAO.listarPorEstado(EstadoVehiculo.DISPONIBLE);
    }

    public List<Vehiculo> listarPorEstado(EstadoVehiculo estado) {
        if (estado == null) {
            throw new DatosInvalidosException("El estado de filtrado no puede ser nulo.");
        }
        return vehiculoDAO.listarPorEstado(estado);
    }

    public List<Vehiculo> listarPorTipo(TipoVehiculo tipo) {
        if (tipo == null) {
            throw new DatosInvalidosException("El tipo de vehículo no puede ser nulo.");
        }
        return vehiculoDAO.listarPorTipo(tipo);
    }

    public List<Vehiculo> listarDisponiblesEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new DatosInvalidosException("El rango de fechas para buscar disponibilidad no es válido.");
        }
        return vehiculoDAO.listarDisponiblesEntre(fechaInicio, fechaFin);
    }

    public boolean actualizarKilometraje(String placa, double nuevoKilometraje) {
        return mantenimientoService.actualizarKilometrajeYVerificar(placa, nuevoKilometraje);
    }

    public void cambiarEstado(String placa, EstadoVehiculo nuevoEstado) {
        if (placa == null || placa.isBlank() || nuevoEstado == null) {
            throw new DatosInvalidosException("La placa y el nuevo estado son obligatorios.");
        }
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(placa.trim().toUpperCase())
                .orElseThrow(() -> new DatosInvalidosException("No existe el vehículo con placa: " + placa));
        vehiculo.setEstado(nuevoEstado);
        vehiculoDAO.actualizar(vehiculo);
    }
}