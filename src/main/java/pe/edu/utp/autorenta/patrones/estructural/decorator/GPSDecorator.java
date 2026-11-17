package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;

// Agrega el costo diario de incluir un dispositivo GPS en el alquiler.
public class GPSDecorator extends TarifaDecorator {
    // Cobertura obtenida del catálogo; contiene el precio configurado por día.
    private final CoberturaSeguro cobertura;
    private final int diasAlquiler;

    public GPSDecorator(TarifaComponent tarifa, CoberturaSeguro cobertura, int diasAlquiler) {
        super(tarifa);
        // El costo del GPS depende de la duración total del alquiler.
        validarDias(diasAlquiler);
        this.cobertura = validarCobertura(cobertura, TipoCobertura.GPS);
        this.diasAlquiler = diasAlquiler;
    }

    @Override
    public double calcularTarifa() {
        // Conserva el monto anterior y agrega GPS por cada día contratado.
        return tarifa.calcularTarifa() + cobertura.calcularCosto(diasAlquiler);
    }

    @Override
    public String getDescripcion() {
        // Mantiene el detalle de extras para mostrarlo luego en la cotización.
        return tarifa.getDescripcion() + " + " + cobertura.getNombre();
    }
}
