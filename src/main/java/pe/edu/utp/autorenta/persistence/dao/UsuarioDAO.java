package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.Optional;
import pe.edu.utp.autorenta.domain.model.Usuario;

public interface UsuarioDAO extends GenericDAO<Usuario, Integer> {
    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);
}
