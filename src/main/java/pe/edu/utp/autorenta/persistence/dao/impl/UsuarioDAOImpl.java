package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.RolUsuario;
import pe.edu.utp.autorenta.domain.model.Usuario;
import pe.edu.utp.autorenta.persistence.dao.UsuarioDAO;

public class UsuarioDAOImpl extends BaseDAO implements UsuarioDAO {

    public UsuarioDAOImpl(ConexionBD conexion){super(conexion);}

    private Usuario mapear(ResultSet rs) throws SQLException{
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre_usuario"),
                rs.getString("password_hash"), RolUsuario.valueOf(rs.getString("rol")), rs.getInt("activo") == 1);
    }

    @Override
    public Usuario guardar(Usuario u){
        u.setIdUsuario(insertar("INSERT INTO usuario (nombre_usuario, password_hash, rol, activo) VALUES (?,?,?,?)",
                u.getNombreUsuario(), u.getPasswordHash(), u.getRol(), u.isActivo()));
        return u;
    }

    @Override
    public void actualizar(Usuario u){
        ejecutar("UPDATE usuario SET password_hash=?, rol=?, activo=? WHERE id_usuario=?",
                u.getPasswordHash(), u.getRol(), u.isActivo(), u.getIdUsuario());
    }

    @Override
    public void eliminar(Integer id){ejecutar("DELETE FROM usuario WHERE id_usuario=?", id);}

    @Override
    public Optional<Usuario> buscarPorId(Integer id){
        return consultarUno("SELECT * FROM usuario WHERE id_usuario=?", this::mapear, id);
    }

    @Override
    public List<Usuario> listarTodos(){return consultar("SELECT * FROM usuario ORDER BY nombre_usuario", this::mapear);}

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombre){
        return consultarUno("SELECT * FROM usuario WHERE nombre_usuario=?", this::mapear, nombre);
    }
}
