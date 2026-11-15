package pe.edu.utp.autorenta.patrones.comportamiento.observer;

import pe.edu.utp.autorenta.domain.model.Vehiculo;

// Patrón GoF: Observer (Interfaz Subject / Publicador de eventos de la flota)
// Principios SOLID: OCP (permite suscribir nuevos observadores sin cambiar el sujeto) y DIP
// GRASP: Bajo Acoplamiento e Indirección
// Autor: Chamorro Casani Oscar Gonzalo
public interface SujetoFlota {

    // Suscribe un observador para recibir notificaciones de mantenimiento
    void agregarObservador(ObservadorMantenimiento observador);

    // Elimina un observador registrado
    void eliminarObservador(ObservadorMantenimiento observador);

    // Notifica a todos los observadores cuando un vehículo alcanza su límite de kilometraje
    void notificarObservadores(Vehiculo vehiculo);
}