package pe.edu.utp.autorenta.patrones.comportamiento.strategy;

import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.ContratoCorporativo;
import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GoF: ConcreteStrategy (Estrategia de cobro para alquileres mensuales y corporativos)
// Principios SOLID: SRP (descuento corporativo y control de km mensual) y LSP (compatible con ContratoCorporativo)
// GRASP: Experto en Información y Polimorfismo
// Autor: Chamorro Casani Oscar Gonzalo
public class AlquilerMensualCorporativo implements EstrategiaCobro {

    public static final double DESCUENTO_CORPORATIVO_POR_DEFECTO = 0.15; // 15% de descuento corporativo
    public static final double RECARGO_KM_EXCESO_POR_DEFECTO = 0.80;     // S/ 0.80 por km excedido

    private final double descuentoCorporativo;
    private final double recargoPorKmExceso;

    public AlquilerMensualCorporativo() {
        this(DESCUENTO_CORPORATIVO_POR_DEFECTO, RECARGO_KM_EXCESO_POR_DEFECTO);
    }

    public AlquilerMensualCorporativo(double descuentoCorporativo, double recargoPorKmExceso) {
        if (descuentoCorporativo < 0 || descuentoCorporativo >= 1) {
            throw new DatosInvalidosException("El descuento corporativo debe estar en el rango [0, 1).");
        }
        if (recargoPorKmExceso < 0) {
            throw new DatosInvalidosException("El recargo por exceso de kilometraje no puede ser negativo.");
        }
        this.descuentoCorporativo = descuentoCorporativo;
        this.recargoPorKmExceso = recargoPorKmExceso;
    }

    @Override
    public double calcularTarifa(Vehiculo vehiculo, int dias) {
        if (vehiculo == null) {
            throw new DatosInvalidosException("El vehículo es obligatorio para calcular la tarifa corporativa.");
        }
        int diasEfectivos = Math.max(1, dias);
        double montoBruto = diasEfectivos * vehiculo.getPrecioBasePorDia();
        return montoBruto * (1.0 - descuentoCorporativo);
    }

    @Override
    public double calcularTarifa(ContratoAlquiler contrato) {
        if (contrato == null || contrato.getVehiculo() == null) {
            throw new DatosInvalidosException("El contrato y su vehículo son obligatorios para calcular la tarifa.");
        }
        int dias = contrato.getDiasAlquiler();
        double montoConDescuento = calcularTarifa(contrato.getVehiculo(), dias);

        // Evaluar límite de kilometraje mensual o pactado
        double limiteKmPermitido = contrato.getKilometrajePactado();
        if (contrato instanceof ContratoCorporativo corporativo && corporativo.getLimiteKilometrajeMensual() > 0) {
            double mesesEquivalentes = Math.max(1.0, Math.ceil(dias / 30.0));
            limiteKmPermitido = corporativo.getLimiteKilometrajeMensual() * mesesEquivalentes;
        }

        double kmRecorridos = contrato.getKilometrosRecorridos();
        if (limiteKmPermitido > 0 && kmRecorridos > limiteKmPermitido) {
            double excesoKm = kmRecorridos - limiteKmPermitido;
            montoConDescuento += excesoKm * recargoPorKmExceso;
        }
        return montoConDescuento;
    }

    @Override
    public TipoCobro getTipoCobro() {
        return TipoCobro.MENSUAL_CORPORATIVO;
    }

    public double getDescuentoCorporativo() {
        return descuentoCorporativo;
    }

    public double getRecargoPorKmExceso() {
        return recargoPorKmExceso;
    }
}