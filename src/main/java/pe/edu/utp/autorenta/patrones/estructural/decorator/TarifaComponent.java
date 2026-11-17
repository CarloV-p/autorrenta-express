package pe.edu.utp.autorenta.patrones.estructural.decorator;

// @author Chirme Ccolque Edwards Alex

// Componente común para la tarifa original y todos los extras que la decoran.
public interface TarifaComponent {

    // Calcula el monto final acumulado hasta el decorador actual.
    double calcularTarifa();

    // Devuelve el detalle legible de los servicios incluidos en la tarifa.
    String getDescripcion();
}
