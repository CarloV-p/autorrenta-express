package pe.edu.utp.autorenta.application.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.Cotizacion;
import pe.edu.utp.autorenta.domain.model.PoliticaDeposito;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.patrones.comportamiento.strategy.AlquilerMensualCorporativo;
import pe.edu.utp.autorenta.patrones.comportamiento.strategy.EstrategiaCobro;
import pe.edu.utp.autorenta.patrones.comportamiento.strategy.KilometrajeLibre;
import pe.edu.utp.autorenta.patrones.comportamiento.strategy.TarifaPorDia;

// Patrón GoF: Contexto del patrón Strategy para seleccionar y ejecutar algoritmos de cobro
// Principios SOLID: SRP (cálculo de tarifas y cotizaciones), OCP y DIP (trabaja con la interfaz EstrategiaCobro)
// GRASP: Experto en Información, Creador (crea objetos Cotizacion) y Alta Cohesión
// Autor: Chamorro Casani Oscar Gonzalo
public class TarifaService {

    private final Map<TipoCobro, EstrategiaCobro> estrategias;
    private EstrategiaCobro estrategiaActual;
    private final PoliticaDeposito politicaDeposito;

    public TarifaService() {
        this(PoliticaDeposito.porDefecto());
    }

    public TarifaService(PoliticaDeposito politicaDeposito) {
        if (politicaDeposito == null) {
            throw new DatosInvalidosException("La política de depósito no puede ser nula.");
        }
        this.politicaDeposito = politicaDeposito;
        this.estrategias = new EnumMap<>(TipoCobro.class);
        registrarEstrategiasPorDefecto();
        this.estrategiaActual = this.estrategias.get(TipoCobro.POR_DIA);
    }

    public TarifaService(EstrategiaCobro estrategiaInicial, PoliticaDeposito politicaDeposito) {
        this(politicaDeposito);
        setEstrategia(estrategiaInicial);
    }

    private void registrarEstrategiasPorDefecto() {
        estrategias.put(TipoCobro.POR_DIA, new TarifaPorDia());
        estrategias.put(TipoCobro.KILOMETRAJE_LIBRE, new KilometrajeLibre());
        estrategias.put(TipoCobro.MENSUAL_CORPORATIVO, new AlquilerMensualCorporativo());
    }

    // Permite registrar nuevas estrategias sin modificar el servicio (OCP)
    public final void registrarEstrategia(TipoCobro tipoCobro, EstrategiaCobro estrategia) {
        if (tipoCobro == null || estrategia == null) {
            throw new DatosInvalidosException("El tipo de cobro y la estrategia son obligatorios.");
        }
        estrategias.put(tipoCobro, estrategia);
    }

    public final void setEstrategia(EstrategiaCobro estrategia) {
        if (estrategia == null) {
            throw new DatosInvalidosException("La estrategia de cobro no puede ser nula.");
        }
        this.estrategiaActual = estrategia;
    }

    // Selecciona dinámicamente la estrategia según el enum TipoCobro
    public EstrategiaCobro seleccionarEstrategia(TipoCobro tipoCobro) {
        if (tipoCobro == null) {
            throw new DatosInvalidosException("El tipo de cobro es obligatorio.");
        }
        EstrategiaCobro seleccionada = estrategias.get(tipoCobro);
        if (seleccionada == null) {
            throw new DatosInvalidosException("No existe una estrategia registrada para el tipo de cobro: " + tipoCobro);
        }
        this.estrategiaActual = seleccionada;
        return seleccionada;
    }

    public double calcularTarifaBase(Vehiculo vehiculo, int dias, TipoCobro tipoCobro) {
        EstrategiaCobro estrategia = seleccionarEstrategia(tipoCobro);
        return estrategia.calcularTarifa(vehiculo, dias);
    }

    // Suma el costo de todas las coberturas o extras seleccionados
    public double calcularCostoCoberturas(List<CoberturaSeguro> coberturas, int dias) {
        if (coberturas == null || coberturas.isEmpty()) {
            return 0.0;
        }
        int diasEfectivos = Math.max(1, dias);
        double totalCoberturas = 0.0;
        for (CoberturaSeguro cobertura : coberturas) {
            if (cobertura != null) {
                totalCoberturas += cobertura.calcularCosto(diasEfectivos);
            }
        }
        return totalCoberturas;
    }

    public double calcularMontoTotal(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin,
                                     TipoCobro tipoCobro, List<CoberturaSeguro> coberturas) {
        validarFechas(fechaInicio, fechaFin);
        int dias = calcularDias(fechaInicio, fechaFin);
        double montoEstrategia = calcularTarifaBase(vehiculo, dias, tipoCobro);
        double montoCoberturas = calcularCostoCoberturas(coberturas, dias);
        return montoEstrategia + montoCoberturas;
    }

    // Calcula el total del contrato incluyendo estrategia, excesos de km y coberturas
    public double calcularMontoContrato(ContratoAlquiler contrato) {
        if (contrato == null) {
            throw new DatosInvalidosException("El contrato no puede ser nulo.");
        }
        EstrategiaCobro estrategia = seleccionarEstrategia(contrato.getTipoCobro());
        double montoEstrategia = estrategia.calcularTarifa(contrato);
        double montoCoberturas = calcularCostoCoberturas(contrato.getCoberturas(), contrato.getDiasAlquiler());
        double montoTotal = montoEstrategia + montoCoberturas;

        contrato.setMontoTotal(montoTotal);
        if (contrato.getDepositoGarantia() <= 0) {
            contrato.setDepositoGarantia(calcularDepositoGarantia(montoTotal));
        }
        return montoTotal;
    }

    public double calcularDepositoGarantia(double montoEstimado) {
        return politicaDeposito.calcularDeposito(Math.max(0, montoEstimado));
    }

    public Cotizacion generarCotizacion(Vehiculo vehiculo, LocalDate fechaInicio, LocalDate fechaFin,
                                        TipoCobro tipoCobro, List<CoberturaSeguro> coberturas) {
        double montoEstimado = calcularMontoTotal(vehiculo, fechaInicio, fechaFin, tipoCobro, coberturas);
        double deposito = calcularDepositoGarantia(montoEstimado);
        return new Cotizacion(vehiculo, fechaInicio, fechaFin, tipoCobro, coberturas, montoEstimado, deposito);
    }

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new DatosInvalidosException("Las fechas de inicio y fin son obligatorias.");
        }
        if (fin.isBefore(inicio)) {
            throw new DatosInvalidosException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    private int calcularDias(LocalDate inicio, LocalDate fin) {
        return (int) Math.max(1, ChronoUnit.DAYS.between(inicio, fin));
    }

    public EstrategiaCobro getEstrategiaActual() {
        return estrategiaActual;
    }

    public PoliticaDeposito getPoliticaDeposito() {
        return politicaDeposito;
    }
}