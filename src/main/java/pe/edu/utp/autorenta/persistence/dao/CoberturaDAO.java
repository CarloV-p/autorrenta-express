package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.Optional;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;

// catalogo de coberturas (existen independientemente de los contratos)
public interface CoberturaDAO extends GenericDAO<CoberturaSeguro, Integer> {
    Optional<CoberturaSeguro> buscarPorTipo(TipoCobertura tipo);
}
