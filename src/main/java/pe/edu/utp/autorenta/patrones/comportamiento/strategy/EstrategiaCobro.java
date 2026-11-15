package pe.edu.utp.autorenta.patrones.comportamiento.strategy;

import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GoF: Strategy (Interfaz de estrategia para el cálculo dinámico de tarifas)
// Principios SOLID: OCP (permite agregar nuevas tarifas sin modificar TarifaService) y DIP (depende de abstracciones)
// GRASP: Polimorfismo y Bajo Acoplamiento
// Autor: Chamorro Casani Oscar Gonzalo
public interface EstrategiaCobro {

    // Calcula la tarifa estimada para una cotización según el vehículo y los días
    double calcularTarifa(Vehiculo vehiculo, int dias);

    // Calcula la tarifa final de un contrato considerando días y posibles excesos de kilometraje
    double calcularTarifa(ContratoAlquiler contrato);

    // Devuelve el tipo de cobro asociado a esta estrategia
    TipoCobro getTipoCobro();
}