package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

// Representa la tarifa del contrato antes de agregar coberturas o equipamiento.
public class TarifaBase implements TarifaComponent {
    // Monto calculado previamente por la regla de negocio o la estrategia de cobro.
    private final double montoBase;

    public TarifaBase(double montoBase) {
        // Una tarifa inicial negativa o no finita no es válida para una cotización.
        if (!Double.isFinite(montoBase) || montoBase < 0) {
            throw new IllegalArgumentException("El monto base no puede ser negativo.");
        }
        this.montoBase = montoBase;
    }

    @Override
    public double calcularTarifa() {
        // No añade costos: entrega el punto de partida de la cadena Decorator.
        return montoBase;
    }

    @Override
    public String getDescripcion() {
        // Esta descripción se amplía cuando se encadenan los decoradores.
        return "Tarifa base";
    }
}
