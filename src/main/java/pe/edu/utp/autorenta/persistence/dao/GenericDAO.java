package pe.edu.utp.autorenta.persistence.dao;
// @author Valdivia Paricahua Carlo Alexander

import java.util.List;
import java.util.Optional;

// CRUD comun
// T = entidad, ID = tipo de su clave primaria
public interface GenericDAO<T, ID> {

    // inserta la entidad y devuelve la misma entidad con su id generado, si aplica
    T guardar(T entidad);
    void actualizar(T entidad);
    void eliminar(ID id);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}
