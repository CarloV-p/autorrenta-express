package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

import java.util.Objects;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;

// Base abstracta de los extras; cada subclase suma su costo a otra tarifa.
public abstract class TarifaDecorator implements TarifaComponent {
    // Componente envuelto: puede ser la tarifa base u otro decorador ya aplicado.
    protected final TarifaComponent tarifa;

    protected TarifaDecorator(TarifaComponent tarifa) {
        // Evita crear un decorador que no tenga una tarifa sobre la cual operar.
        this.tarifa = Objects.requireNonNull(tarifa, "La tarifa a decorar no puede ser nula.");
    }

    // Todos los costos de extras se calculan por días de alquiler válidos.
    protected static void validarDias(int diasAlquiler) {
        if (diasAlquiler < 1) {
            throw new IllegalArgumentException("Los días de alquiler deben ser al menos uno.");
        }
    }

    // Impide aplicar un decorador con una cobertura que corresponde a otro extra.
    protected static CoberturaSeguro validarCobertura(CoberturaSeguro cobertura, TipoCobertura tipoEsperado) {
        Objects.requireNonNull(cobertura, "La cobertura no puede ser nula.");
        if (cobertura.getTipo() != tipoEsperado) {
            throw new IllegalArgumentException("La cobertura no corresponde a " + tipoEsperado + ".");
        }
        return cobertura;
    }
}
