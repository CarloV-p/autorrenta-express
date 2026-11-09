package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.time.LocalDate;
import java.util.List;
import pe.edu.utp.autorenta.domain.enums.EstadoContrato;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;

/*
    guarda y recupera contratos simples Y corporativos 
    (ContratoCorporativo es un ContratoAlquiler, por eso basta una sola interfaz -> LSP)
    guardar() persiste en una transacción -> contrato + conductores + coberturas + tramos
 */
public interface ContratoDAO extends GenericDAO<ContratoAlquiler, Integer> {
    List<ContratoAlquiler> listarPorCliente(int idCliente);
    List<ContratoAlquiler> listarPorVehiculo(String placa);
    List<ContratoAlquiler> listarPorEstado(EstadoContrato estado);
    
    // true si el vehículo ya tiene un contrato ACTIVO que se cruza con [inicio, fin]
    boolean existeContratoActivoEnRango(String placa, LocalDate inicio, LocalDate fin);
    void actualizarEstado(int idContrato, EstadoContrato nuevoEstado);
}
