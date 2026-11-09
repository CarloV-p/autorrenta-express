package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.model.Cliente;
import pe.edu.utp.autorenta.persistence.dao.ClienteDAO;

public class ClienteDAOImpl extends BaseDAO implements ClienteDAO {
    public ClienteDAOImpl(ConexionBD conexion){super(conexion);}

    private Cliente mapear(ResultSet rs) throws SQLException{
        return new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"),
                rs.getString("dni"), rs.getString("telefono"));
    }

    @Override
    public Cliente guardar(Cliente c){
        c.setIdCliente(insertar("INSERT INTO cliente (nombre, dni, telefono) VALUES (?,?,?)",
                c.getNombre(), c.getDni(), c.getTelefono()));
        return c;
    }

    @Override
    public void actualizar(Cliente c){
        ejecutar("UPDATE cliente SET nombre=?, dni=?, telefono=? WHERE id_cliente=?",
                c.getNombre(), c.getDni(), c.getTelefono(), c.getIdCliente());
    }

    @Override
    public void eliminar(Integer id){ejecutar("DELETE FROM cliente WHERE id_cliente=?", id);}

    @Override
    public Optional<Cliente> buscarPorId(Integer id){
        return consultarUno("SELECT * FROM cliente WHERE id_cliente=?", this::mapear, id);
    }

    @Override
    public List<Cliente> listarTodos(){return consultar("SELECT * FROM cliente ORDER BY nombre", this::mapear);}

    @Override
    public Optional<Cliente> buscarPorDni(String dni){
        return consultarUno("SELECT * FROM cliente WHERE dni=?", this::mapear, dni);
    }
}
