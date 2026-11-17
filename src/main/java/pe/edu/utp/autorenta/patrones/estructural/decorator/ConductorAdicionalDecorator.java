package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;

// Agrega el costo diario de registrar un conductor adicional en el contrato.
public class ConductorAdicionalDecorator extends TarifaDecorator {
    // Cobertura obtenida del catálogo; contiene el precio configurado por día.
    private final CoberturaSeguro cobertura;
    private final int diasAlquiler;

    public ConductorAdicionalDecorator(TarifaComponent tarifa, CoberturaSeguro cobertura, int diasAlquiler) {
        super(tarifa);
        validarDias(diasAlquiler);
        this.cobertura = validarCobertura(cobertura, TipoCobertura.CONDUCTOR_ADICIONAL);
        this.diasAlquiler = diasAlquiler;
    }

    @Override
    public double calcularTarifa() {
        // Suma el valor diario del conductor adicional al monto ya acumulado.
        return tarifa.calcularTarifa() + cobertura.calcularCosto(diasAlquiler);
    }

    @Override
    public String getDescripcion() {
        return tarifa.getDescripcion() + " + " + cobertura.getNombre();
    }
}
