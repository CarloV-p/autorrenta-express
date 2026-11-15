package pe.edu.utp.autorenta.patrones.comportamiento.strategy;

import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GoF: ConcreteStrategy (Estrategia concreta de cobro estándar por día)
// Principios SOLID: SRP (solo calcula la tarifa diaria) y LSP (sustituye a EstrategiaCobro)
// GRASP: Experto en Información y Alta Cohesión
// Autor: Chamorro Casani Oscar Gonzalo
public class TarifaPorDia implements EstrategiaCobro {

    public static final double RECARGO_KM_EXTRA_POR_DEFECTO = 0.50;

    private final double recargoPorKmExtra;

    // Constructor por defecto con recargo estándar de S/ 0.50 por km excedido
    public TarifaPorDia() {
        this(RECARGO_KM_EXTRA_POR_DEFECTO);
    }

    public TarifaPorDia(double recargoPorKmExtra) {
        if (recargoPorKmExtra < 0) {
            throw new DatosInvalidosException("El recargo por kilómetro extra no puede ser negativo.");
        }
        this.recargoPorKmExtra = recargoPorKmExtra;
    }

    @Override
    public double calcularTarifa(Vehiculo vehiculo, int dias) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo es obligatorio para calcular la tarifa por día.");
        }
        int diasEfectivos = Math.max(1, dias);
        return diasEfectivos * vehiculo.getPrecioBasePorDia();
    }

    @Override
    public double calcularTarifa(ContratoAlquiler contrato) {
        if (contrato == null || contrato.getVehiculo() == null) {
            throw new DatosInvalidosException("El contrato y su vehículo son obligatorios para calcular la tarifa.");
        }
        double montoBase = calcularTarifa(contrato.getVehiculo(), contrato.getDiasAlquiler());

        // Si el contrato tiene límite de kilometraje pactado y se excedió al devolver el vehículo, se suma el recargo
        double kmPactado = contrato.getKilometrajePactado();
        double kmRecorridos = contrato.getKilometrosRecorridos();
        if (kmPactado > 0 && kmRecorridos > kmPactado) {
            double excesoKm = kmRecorridos - kmPactado;
            montoBase += excesoKm * recargoPorKmExtra;
        }
        return montoBase;
    }

    @Override
    public TipoCobro getTipoCobro() {
        return TipoCobro.POR_DIA;
    }

    public double getRecargoPorKmExtra() {
        return recargoPorKmExtra;
    }
}