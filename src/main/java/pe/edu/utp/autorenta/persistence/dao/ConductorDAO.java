package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.Optional;
import pe.edu.utp.autorenta.domain.model.Conductor;

public interface ConductorDAO extends GenericDAO<Conductor, Integer> {
    Optional<Conductor> buscarPorDni(String dni);
    Optional<Conductor> buscarPorNroLicencia(String nroLicencia);
}
