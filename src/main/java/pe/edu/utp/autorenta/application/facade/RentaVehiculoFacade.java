package pe.edu.utp.autorenta.application.facade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.application.service.MantenimientoService;
import pe.edu.utp.autorenta.application.service.TarifaService;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoContrato;
import pe.edu.utp.autorenta.domain.enums.MetodoPago;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.exception.LicenciaInvalidaException;
import pe.edu.utp.autorenta.domain.exception.VehiculoNoDisponibleException;
import pe.edu.utp.autorenta.domain.model.Cliente;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;
import pe.edu.utp.autorenta.domain.model.Conductor;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.Cotizacion;
import pe.edu.utp.autorenta.domain.model.Pago;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.persistence.dao.ClienteDAO;
import pe.edu.utp.autorenta.persistence.dao.CoberturaDAO;
import pe.edu.utp.autorenta.persistence.dao.ConductorDAO;
import pe.edu.utp.autorenta.persistence.dao.ContratoDAO;
import pe.edu.utp.autorenta.persistence.dao.PagoDAO;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.ClienteDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.CoberturaDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.ConductorDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.ContratoDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.PagoDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.VehiculoDAOImpl;

// Patrón GoF: Facade (Unifica y simplifica los subsistemas de alquiler, pagos, tarifas y mantenimiento)
// Principios SOLID: SRP (orquesta el flujo de contratación) y DIP (depende de interfaces DAO y servicios)
// GRASP: Bajo Acoplamiento, Alta Cohesión e Indirección
// Autor: Chamorro Casani Oscar Gonzalo
public class RentaVehiculoFacade {

    private final VehiculoDAO vehiculoDAO;
    private final ClienteDAO clienteDAO;
    private final ConductorDAO conductorDAO;
    private final CoberturaDAO coberturaDAO;
    private final ContratoDAO contratoDAO;
    private final PagoDAO pagoDAO;
    private final TarifaService tarifaService;
    private final MantenimientoService mantenimientoService;

    public RentaVehiculoFacade() {
        this(crearConexionInicializada());
    }

    public RentaVehiculoFacade(ConexionBD conexion) {
        if (conexion == null) {
            throw new DatosInvalidosException("La conexión a la base de datos no puede ser nula.");
        }
        this.vehiculoDAO = new VehiculoDAOImpl(conexion);
        this.clienteDAO = new ClienteDAOImpl(conexion);
        this.conductorDAO = new ConductorDAOImpl(conexion);
        this.coberturaDAO = new CoberturaDAOImpl(conexion);
        this.contratoDAO = new ContratoDAOImpl(conexion, this.clienteDAO, this.vehiculoDAO, this.conductorDAO, this.coberturaDAO);
        this.pagoDAO = new PagoDAOImpl(conexion);
        this.tarifaService = new TarifaService();
        this.mantenimientoService = new MantenimientoService(conexion);
    }

    // Constructor con Inyección de Dependencias (DIP)
    public RentaVehiculoFacade(VehiculoDAO vehiculoDAO,
                               ClienteDAO clienteDAO,
                               ConductorDAO conductorDAO,
                               CoberturaDAO coberturaDAO,
                               ContratoDAO contratoDAO,
                               PagoDAO pagoDAO,
                               TarifaService tarifaService,
                               MantenimientoService mantenimientoService) {
        if (vehiculoDAO == null || clienteDAO == null || conductorDAO == null || coberturaDAO == null
                || contratoDAO == null || pagoDAO == null || tarifaService == null || mantenimientoService == null) {
            throw new DatosInvalidosException("Ninguna dependencia de RentaVehiculoFacade puede ser nula.");
        }
        this.vehiculoDAO = vehiculoDAO;
        this.clienteDAO = clienteDAO;
        this.conductorDAO = conductorDAO;
        this.coberturaDAO = coberturaDAO;
        this.contratoDAO = contratoDAO;
        this.pagoDAO = pagoDAO;
        this.tarifaService = tarifaService;
        this.mantenimientoService = mantenimientoService;
    }

    private static ConexionBD crearConexionInicializada() {
        ConexionBD bd = new ConexionBD();
        bd.inicializarEsquema();
        return bd;
    }

    public Cotizacion cotizarAlquiler(String placa, LocalDate fechaInicio, LocalDate fechaFin,
                                      TipoCobro tipoCobro, List<TipoCobertura> tiposCobertura) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("Debe especificar la placa del vehículo para cotizar.");
        }
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(placa.trim().toUpperCase())
                .orElseThrow(() -> new DatosInvalidosException("No existe el vehículo con placa: " + placa));

        List<CoberturaSeguro> coberturas = resolverCoberturasPorTipo(tiposCobertura);
        return tarifaService.generarCotizacion(vehiculo, fechaInicio, fechaFin, tipoCobro, coberturas);
    }

    public Cotizacion cotizarAlquiler(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin,
                                      TipoCobro tipoCobro, List<CoberturaSeguro> coberturas) {
        return tarifaService.generarCotizacion(vehiculo, fechaInicio, fechaFin, tipoCobro, coberturas);
    }

    // Ejecuta todo el proceso de alquiler: validaciones, cálculo con Strategy, guardado en BD y pago
    public ContratoAlquiler registrarAlquiler(ContratoAlquiler contrato, MetodoPago metodoPago) {
        if (contrato == null) {
            throw new DatosInvalidosException("El contrato a registrar no puede ser nulo.");
        }

        // 1. Verificar disponibilidad del vehículo
        Vehiculo vehiculoBD = vehiculoDAO.buscarPorId(contrato.getVehiculo().getPlaca())
                .orElseThrow(() -> new DatosInvalidosException("El vehículo " + contrato.getVehiculo().getPlaca() + " no está registrado."));

        if (!vehiculoBD.estaDisponible() || contratoDAO.existeContratoActivoEnRango(
                vehiculoBD.getPlaca(), contrato.getFechaInicio(), contrato.getFechaFin())) {
            throw new VehiculoNoDisponibleException(vehiculoBD.getPlaca());
        }

        // 2. Asegurar persistencia del cliente
        Cliente cliente = asegurarClientePersistido(contrato.getCliente());
        contrato.setCliente(cliente);

        // 3. Validar licencias y asegurar persistencia de conductores
        validarYAsegurarConductores(contrato);

        // 4. Sincronizar coberturas con catálogo
        sincronizarCoberturasConCatalogo(contrato);

        // 5. Calcular tarifa y depósito usando TarifaService (Strategy)
        contrato.setKilometrajeInicial(vehiculoBD.getKilometraje());
        tarifaService.calcularMontoContrato(contrato);

        // 6. Guardar contrato en base de datos
        ContratoAlquiler contratoGuardado = contratoDAO.guardar(contrato);

        // 7. Cambiar estado del vehículo a ALQUILADO
        vehiculoBD.marcarAlquilado();
        vehiculoDAO.actualizar(vehiculoBD);
        contratoGuardado.setVehiculo(vehiculoBD);

        // 8. Registrar pago inicial
        if (contratoGuardado.getMontoTotal() > 0) {
            MetodoPago metodoEfectivo = metodoPago != null ? metodoPago : MetodoPago.EFECTIVO;
            Pago pago = new Pago(contratoGuardado.getIdContrato(), LocalDate.now(),
                    contratoGuardado.getMontoTotal(), metodoEfectivo);
            pagoDAO.registrar(pago);
        }

        return contratoGuardado;
    }

    public ContratoAlquiler registrarAlquilerSimple(String dniCliente, String placa,
                                                    LocalDate fechaInicio, LocalDate fechaFin,
                                                    TipoCobro tipoCobro, List<String> dnisConductores,
                                                    List<TipoCobertura> tiposCobertura,
                                                    MetodoPago metodoPago) {
        if (dniCliente == null || dniCliente.isBlank()) {
            throw new DatosInvalidosException("El DNI del cliente es obligatorio.");
        }
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("La placa del vehículo es obligatoria.");
        }

        Cliente cliente = clienteDAO.buscarPorDni(dniCliente.trim())
                .orElseThrow(() -> new DatosInvalidosException("No se encontró un cliente con DNI: " + dniCliente));
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(placa.trim().toUpperCase())
                .orElseThrow(() -> new DatosInvalidosException("No se encontró un vehículo con placa: " + placa));

        ContratoAlquiler contrato = new ContratoAlquiler(cliente, vehiculo, fechaInicio, fechaFin);
        contrato.setTipoCobro(tipoCobro != null ? tipoCobro : TipoCobro.POR_DIA);

        if (dnisConductores != null) {
            for (String dniCond : dnisConductores) {
                if (dniCond != null && !dniCond.isBlank()) {
                    Conductor conductor = conductorDAO.buscarPorDni(dniCond.trim())
                            .orElseThrow(() -> new DatosInvalidosException("No existe conductor con DNI: " + dniCond));
                    contrato.agregarConductor(conductor);
                }
            }
        }

        for (CoberturaSeguro cobertura : resolverCoberturasPorTipo(tiposCobertura)) {
            contrato.agregarCobertura(cobertura);
        }

        return registrarAlquiler(contrato, metodoPago);
    }

    // Finaliza el alquiler, cobra exceso de km si aplica y actualiza el odómetro disparando el Observer
    public ContratoAlquiler finalizarAlquilerYActualizarKilometraje(int idContrato,
                                                                    double kilometrajeDevolucion,
                                                                    MetodoPago metodoPagoDiferencia) {
        ContratoAlquiler contrato = contratoDAO.buscarPorId(idContrato)
                .orElseThrow(() -> new DatosInvalidosException("No existe el contrato con ID: " + idContrato));

        double montoInicial = contrato.getMontoTotal();

        // 1. Finalizar contrato registrando kilometraje de devolución
        contrato.finalizar(kilometrajeDevolucion);

        // 2. Recalcular tarifa con Strategy por posible exceso de kilometraje
        double montoFinal = tarifaService.calcularMontoContrato(contrato);
        contratoDAO.actualizar(contrato);

        // 3. Registrar pago diferencial si hubo exceso de kilometraje
        double diferencia = montoFinal - montoInicial;
        if (diferencia > 0.01) {
            MetodoPago metodo = metodoPagoDiferencia != null ? metodoPagoDiferencia : MetodoPago.EFECTIVO;
            pagoDAO.registrar(new Pago(contrato.getIdContrato(), LocalDate.now(), diferencia, metodo));
        }

        // 4. Liberar vehículo a DISPONIBLE y verificar alerta de mantenimiento (Observer)
        String placa = contrato.getVehiculo().getPlaca();
        Vehiculo vehiculo = vehiculoDAO.buscarPorId(placa)
                .orElseThrow(() -> new DatosInvalidosException("No existe el vehículo con placa: " + placa));
        vehiculo.marcarDisponible();
        vehiculoDAO.actualizar(vehiculo);

        mantenimientoService.actualizarKilometrajeYVerificar(placa, kilometrajeDevolucion);

        vehiculoDAO.buscarPorId(placa).ifPresent(contrato::setVehiculo);
        return contrato;
    }

    public ContratoAlquiler finalizarAlquilerYActualizarKilometraje(int idContrato, double kilometrajeDevolucion) {
        return finalizarAlquilerYActualizarKilometraje(idContrato, kilometrajeDevolucion, MetodoPago.EFECTIVO);
    }

    public void cancelarAlquiler(int idContrato) {
        ContratoAlquiler contrato = contratoDAO.buscarPorId(idContrato)
                .orElseThrow(() -> new DatosInvalidosException("No existe el contrato con ID: " + idContrato));

        contrato.cancelar();
        contratoDAO.actualizarEstado(idContrato, EstadoContrato.CANCELADO);

        vehiculoDAO.buscarPorId(contrato.getVehiculo().getPlaca()).ifPresent(v -> {
            v.marcarDisponible();
            vehiculoDAO.actualizar(v);
        });
    }

    public List<ContratoAlquiler> listarContratos() {
        return contratoDAO.listarTodos();
    }

    public List<ContratoAlquiler> listarContratosPorEstado(EstadoContrato estado) {
        if (estado == null) {
            throw new DatosInvalidosException("El estado de contrato es obligatorio.");
        }
        return contratoDAO.listarPorEstado(estado);
    }

    public List<ContratoAlquiler> listarContratosPorCliente(int idCliente) {
        return contratoDAO.listarPorCliente(idCliente);
    }

    public List<ContratoAlquiler> listarContratosPorVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new DatosInvalidosException("La placa es obligatoria.");
        }
        return contratoDAO.listarPorVehiculo(placa.trim().toUpperCase());
    }

    public Optional<ContratoAlquiler> buscarContratoPorId(int idContrato) {
        return contratoDAO.buscarPorId(idContrato);
    }

    public List<Vehiculo> listarVehiculosDisponibles(LocalDate inicio, LocalDate fin) {
        return vehiculoDAO.listarDisponiblesEntre(inicio, fin);
    }

    public List<CoberturaSeguro> listarCatalogoCoberturas() {
        return coberturaDAO.listarTodos();
    }

    public List<Pago> listarPagosPorContrato(int idContrato) {
        return pagoDAO.listarPorContrato(idContrato);
    }

    private Cliente asegurarClientePersistido(Cliente cliente) {
        if (cliente == null) {
            throw new DatosInvalidosException("El cliente del contrato es obligatorio.");
        }
        if (cliente.getIdCliente() > 0) {
            return cliente;
        }
        return clienteDAO.buscarPorDni(cliente.getDni())
                .orElseGet(() -> clienteDAO.guardar(cliente));
    }

    private void validarYAsegurarConductores(ContratoAlquiler contrato) {
        LocalDate fechaReferencia = contrato.getFechaFin() != null ? contrato.getFechaFin() : LocalDate.now();
        for (Conductor conductor : contrato.getConductores()) {
            if (conductor.getFechaVencimientoLicencia() != null && !conductor.licenciaVigente(fechaReferencia)) {
                throw new LicenciaInvalidaException(conductor.getNroLicencia(),
                        "Vence antes de la finalización del alquiler (" + conductor.getFechaVencimientoLicencia() + ").");
            }
            if (conductor.getIdConductor() <= 0) {
                Optional<Conductor> existente = conductorDAO.buscarPorDni(conductor.getDni());
                if (existente.isPresent()) {
                    conductor.setIdConductor(existente.get().getIdConductor());
                } else {
                    conductorDAO.guardar(conductor);
                }
            }
        }
    }

    private void sincronizarCoberturasConCatalogo(ContratoAlquiler contrato) {
        for (CoberturaSeguro cobertura : contrato.getCoberturas()) {
            if (cobertura.getIdCobertura() <= 0) {
                coberturaDAO.buscarPorTipo(cobertura.getTipo())
                        .ifPresent(cat -> cobertura.setIdCobertura(cat.getIdCobertura()));
            }
        }
    }

    private List<CoberturaSeguro> resolverCoberturasPorTipo(List<TipoCobertura> tiposCobertura) {
        List<CoberturaSeguro> resultado = new ArrayList<>();
        if (tiposCobertura == null || tiposCobertura.isEmpty()) {
            return resultado;
        }
        for (TipoCobertura tipo : tiposCobertura) {
            if (tipo != null) {
                CoberturaSeguro cob = coberturaDAO.buscarPorTipo(tipo)
                        .orElseThrow(() -> new DatosInvalidosException("Cobertura no encontrada en catálogo: " + tipo));
                resultado.add(cob);
            }
        }
        return resultado;
    }

    public TarifaService getTarifaService() {
        return tarifaService;
    }

    public MantenimientoService getMantenimientoService() {
        return mantenimientoService;
    }
}