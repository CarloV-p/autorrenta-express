package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.Optional;
import pe.edu.utp.autorenta.domain.model.Cliente;

public interface ClienteDAO extends GenericDAO<Cliente, Integer> {
    Optional<Cliente> buscarPorDni(String dni);
}
