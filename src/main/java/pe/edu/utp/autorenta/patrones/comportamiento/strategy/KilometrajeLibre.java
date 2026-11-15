package pe.edu.utp.autorenta.patrones.comportamiento.strategy;

import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GoF: ConcreteStrategy (Estrategia de cobro con kilometraje ilimitado)
// Principios SOLID: SRP (fórmula de recargo por km libre) y OCP (extiende EstrategiaCobro sin alterar otras clases)
// GRASP: Polimorfismo y Alta Cohesión
// Autor: Chamorro Casani Oscar Gonzalo
public class KilometrajeLibre implements EstrategiaCobro {

    public static final double RECARGO_PORCENTUAL_POR_DEFECTO = 0.25; // 25% adicional por km ilimitado

    private final double recargoPorcentual;

    // Constructor por defecto que aplica un 25% adicional sobre la tarifa diaria
    public KilometrajeLibre() {
        this(RECARGO_PORCENTUAL_POR_DEFECTO);
    }

    public KilometrajeLibre(double recargoPorcentual) {
        if (recargoPorcentual < 0) {
            throw new DatosInvalidosException("El recargo porcentual por kilometraje libre no puede ser negativo.");
        }
        this.recargoPorcentual = recargoPorcentual;
    }

    @Override
    public double calcularTarifa(Vehiculo vehiculo, int dias) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo es obligatorio para calcular la tarifa de kilometraje libre.");
        }
        int diasEfectivos = Math.max(1, dias);
        double subtotalDiario = diasEfectivos * vehiculo.getPrecioBasePorDia();
        return subtotalDiario * (1.0 + recargoPorcentual);
    }

    @Override
    public double calcularTarifa(ContratoAlquiler contrato) {
        if (contrato == null || contrato.getVehiculo() == null) {
            throw new DatosInvalidosException("El contrato y su vehículo son obligatorios para calcular la tarifa.");
        }
        return calcularTarifa(contrato.getVehiculo(), contrato.getDiasAlquiler());
    }

    @Override
    public TipoCobro getTipoCobro() {
        return TipoCobro.KILOMETRAJE_LIBRE;
    }

    public double getRecargoPorcentual() {
        return recargoPorcentual;
    }
}