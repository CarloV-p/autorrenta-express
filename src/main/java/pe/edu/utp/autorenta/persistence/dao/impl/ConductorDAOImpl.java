package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.model.Conductor;
import pe.edu.utp.autorenta.persistence.dao.ConductorDAO;

public class ConductorDAOImpl extends BaseDAO implements ConductorDAO {
    public ConductorDAOImpl(ConexionBD conexion){super(conexion);}

    private Conductor mapear(ResultSet rs) throws SQLException{
        return new Conductor(rs.getInt("id_conductor"), rs.getString("dni"), rs.getString("nombre"),
                rs.getString("nro_licencia"), fecha(rs, "fecha_vencimiento_licencia"));
    }

    @Override
    public Conductor guardar(Conductor c){
        c.setIdConductor(insertar(
                "INSERT INTO conductor (dni, nombre, nro_licencia, fecha_vencimiento_licencia) VALUES (?,?,?,?)",
                c.getDni(), c.getNombre(), c.getNroLicencia(), c.getFechaVencimientoLicencia()));
        return c;
    }

    @Override
    public void actualizar(Conductor c){
        ejecutar("UPDATE conductor SET dni=?, nombre=?, nro_licencia=?, fecha_vencimiento_licencia=? WHERE id_conductor=?",
                c.getDni(), c.getNombre(), c.getNroLicencia(), c.getFechaVencimientoLicencia(), c.getIdConductor());
    }

    @Override
    public void eliminar(Integer id){ejecutar("DELETE FROM conductor WHERE id_conductor=?", id);}

    @Override
    public Optional<Conductor> buscarPorId(Integer id){
        return consultarUno("SELECT * FROM conductor WHERE id_conductor=?", this::mapear, id);
    }

    @Override
    public List<Conductor> listarTodos(){return consultar("SELECT * FROM conductor ORDER BY nombre", this::mapear);}

    @Override
    public Optional<Conductor> buscarPorDni(String dni){
        return consultarUno("SELECT * FROM conductor WHERE dni=?", this::mapear, dni);
    }

    @Override
    public Optional<Conductor> buscarPorNroLicencia(String nro){
        return consultarUno("SELECT * FROM conductor WHERE nro_licencia=?", this::mapear, nro);
    }
}
