package pe.edu.utp.autorenta.application.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.application.facade.RentaVehiculoFacade;
import pe.edu.utp.autorenta.application.service.TarifaService;
import pe.edu.utp.autorenta.domain.enums.EstadoContrato;
import pe.edu.utp.autorenta.domain.enums.MetodoPago;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.Cotizacion;
import pe.edu.utp.autorenta.domain.model.Pago;
import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GRASP: Controlador (Recibe los eventos de la vista Swing para cotizaciones y contratos)
// Principios SOLID: SRP (separa la vista de la lógica de negocio) y DIP
// Autor: Chamorro Casani Oscar Gonzalo
public class AlquilerController {

    private final RentaVehiculoFacade rentaFacade;
    private final TarifaService tarifaService;

    public AlquilerController() {
        this.rentaFacade = new RentaVehiculoFacade();
        this.tarifaService = this.rentaFacade.getTarifaService();
    }

    public AlquilerController(RentaVehiculoFacade rentaFacade, TarifaService tarifaService) {
        if (rentaFacade == null || tarifaService == null) {
            throw new DatosInvalidosException("RentaVehiculoFacade y TarifaService no pueden ser nulos.");
        }
        this.rentaFacade = rentaFacade;
        this.tarifaService = tarifaService;
    }

    public Cotizacion cotizarAlquiler(String placa, LocalDate fechaInicio, LocalDate fechaFin,
                                      TipoCobro tipoCobro, List<TipoCobertura> tiposCobertura) {
        return rentaFacade.cotizarAlquiler(placa, fechaInicio, fechaFin, tipoCobro, tiposCobertura);
    }

    public Cotizacion cotizarAlquiler(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin,
                                      TipoCobro tipoCobro, List<CoberturaSeguro> coberturas) {
        return tarifaService.generarCotizacion(vehiculo, fechaInicio, fechaFin, tipoCobro, coberturas);
    }

    public ContratoAlquiler crearContratoAlquiler(ContratoAlquiler contrato, MetodoPago metodoPago) {
        return rentaFacade.registrarAlquiler(contrato, metodoPago);
    }

    public ContratoAlquiler registrarAlquilerSimple(String dniCliente, String placa,
                                                    LocalDate fechaInicio, LocalDate fechaFin,
                                                    TipoCobro tipoCobro, List<String> dnisConductores,
                                                    List<TipoCobertura> tiposCobertura,
                                                    MetodoPago metodoPago) {
        return rentaFacade.registrarAlquilerSimple(
                dniCliente, placa, fechaInicio, fechaFin, tipoCobro,
                dnisConductores, tiposCobertura, metodoPago);
    }

    public ContratoAlquiler finalizarAlquiler(int idContrato, double kilometrajeDevolucion) {
        return rentaFacade.finalizarAlquilerYActualizarKilometraje(idContrato, kilometrajeDevolucion);
    }

    public ContratoAlquiler finalizarAlquiler(int idContrato, double kilometrajeDevolucion, MetodoPago metodoPago) {
        return rentaFacade.finalizarAlquilerYActualizarKilometraje(idContrato, kilometrajeDevolucion, metodoPago);
    }

    public void cancelarAlquiler(int idContrato) {
        rentaFacade.cancelarAlquiler(idContrato);
    }

    public List<ContratoAlquiler> listarContratos() {
        return rentaFacade.listarContratos();
    }

    public List<ContratoAlquiler> listarContratosActivos() {
        return rentaFacade.listarContratosPorEstado(EstadoContrato.ACTIVO);
    }

    public List<ContratoAlquiler> listarContratosPorEstado(EstadoContrato estado) {
        return rentaFacade.listarContratosPorEstado(estado);
    }

    public List<ContratoAlquiler> listarContratosPorCliente(int idCliente) {
        return rentaFacade.listarContratosPorCliente(idCliente);
    }

    public List<ContratoAlquiler> listarContratosPorVehiculo(String placa) {
        return rentaFacade.listarContratosPorVehiculo(placa);
    }

    public Optional<ContratoAlquiler> buscarContratoPorId(int idContrato) {
        return rentaFacade.buscarContratoPorId(idContrato);
    }

    public List<CoberturaSeguro> listarCoberturasDisponibles() {
        return rentaFacade.listarCatalogoCoberturas();
    }

    public List<Pago> listarPagosDeContrato(int idContrato) {
        return rentaFacade.listarPagosPorContrato(idContrato);
    }

    public RentaVehiculoFacade getRentaFacade() {
        return rentaFacade;
    }

    public TarifaService getTarifaService() {
        return tarifaService;
    }
}