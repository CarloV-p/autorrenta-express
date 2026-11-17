package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;

// Agrega el costo diario de la cobertura de seguro todo riesgo.
public class SeguroTodoRiesgoDecorator extends TarifaDecorator {
    // Cobertura obtenida del catálogo; contiene el precio configurado por día.
    private final CoberturaSeguro cobertura;
    private final int diasAlquiler;

    public SeguroTodoRiesgoDecorator(TarifaComponent tarifa, CoberturaSeguro cobertura, int diasAlquiler) {
        super(tarifa);
        // El seguro se cobra por todos los días cubiertos por el contrato.
        validarDias(diasAlquiler);
        this.cobertura = validarCobertura(cobertura, TipoCobertura.TODO_RIESGO);
        this.diasAlquiler = diasAlquiler;
    }

    @Override
    public double calcularTarifa() {
        // Suma el seguro al total que ya tenía la tarifa envuelta.
        return tarifa.calcularTarifa() + cobertura.calcularCosto(diasAlquiler);
    }

    @Override
    public String getDescripcion() {
        // Agrega el seguro sin perder el detalle de servicios previos.
        return tarifa.getDescripcion() + " + " + cobertura.getNombre();
    }
}
