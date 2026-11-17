package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;

// Agrega el costo diario de una silla de bebé como equipamiento opcional.
public class SillaBebeDecorator extends TarifaDecorator {
    // Cobertura obtenida del catálogo; contiene el precio configurado por día.
    private final CoberturaSeguro cobertura;
    private final int diasAlquiler;

    public SillaBebeDecorator(TarifaComponent tarifa, CoberturaSeguro cobertura, int diasAlquiler) {
        super(tarifa);
        // Se valida antes de calcular para no cotizar un alquiler de cero días.
        validarDias(diasAlquiler);
        this.cobertura = validarCobertura(cobertura, TipoCobertura.SILLA_BEBE);
        this.diasAlquiler = diasAlquiler;
    }

    @Override
    public double calcularTarifa() {
        // Añade el costo de la silla al monto acumulado hasta este punto.
        return tarifa.calcularTarifa() + cobertura.calcularCosto(diasAlquiler);
    }

    @Override
    public String getDescripcion() {
        // Incluye la silla en el detalle final de la cotización.
        return tarifa.getDescripcion() + " + " + cobertura.getNombre();
    }
}
