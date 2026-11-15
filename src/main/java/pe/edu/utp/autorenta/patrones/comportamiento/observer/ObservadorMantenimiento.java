package pe.edu.utp.autorenta.patrones.comportamiento.observer;

import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GoF: Observer (Interfaz Suscriptor / Observador de alertas de kilometraje)
// Principios SOLID: ISP (interfaz específica con un solo método) y DIP
// GRASP: Polimorfismo y Bajo Acoplamiento
// Autor: Chamorro Casani Oscar Gonzalo
public interface ObservadorMantenimiento {

    // Se ejecuta automáticamente cuando un vehículo alcanza o supera el kilometraje de mantenimiento
    void actualizar(Vehiculo vehiculo);
}