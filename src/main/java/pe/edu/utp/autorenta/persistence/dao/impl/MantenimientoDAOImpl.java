package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoMantenimiento;
import pe.edu.utp.autorenta.domain.enums.TipoMantenimiento;
import pe.edu.utp.autorenta.domain.model.Mantenimiento;
import pe.edu.utp.autorenta.persistence.dao.MantenimientoDAO;

public class MantenimientoDAOImpl extends BaseDAO implements MantenimientoDAO {
    
    public MantenimientoDAOImpl(ConexionBD conexion){super(conexion);}

    private Mantenimiento mapear(ResultSet rs) throws SQLException{
        return new Mantenimiento(rs.getInt("id_mantenimiento"), rs.getString("placa"), fecha(rs, "fecha"),
                TipoMantenimiento.valueOf(rs.getString("tipo")), EstadoMantenimiento.valueOf(rs.getString("estado")),
                rs.getDouble("costo"), rs.getString("descripcion"));
    }

    @Override
    public Mantenimiento guardar(Mantenimiento m){
        m.setIdMantenimiento(insertar(
                "INSERT INTO mantenimiento (placa, fecha, tipo, estado, costo, descripcion) VALUES (?,?,?,?,?,?)",
                m.getPlaca(), m.getFecha(), m.getTipo(), m.getEstado(), m.getCosto(), m.getDescripcion()));
        return m;
    }

    @Override
    public void actualizar(Mantenimiento m){
        ejecutar("UPDATE mantenimiento SET estado=?, costo=?, descripcion=? WHERE id_mantenimiento=?",
                m.getEstado(), m.getCosto(), m.getDescripcion(), m.getIdMantenimiento());
    }

    @Override
    public void eliminar(Integer id){ejecutar("DELETE FROM mantenimiento WHERE id_mantenimiento=?", id);}

    @Override
    public Optional<Mantenimiento> buscarPorId(Integer id){
        return consultarUno("SELECT * FROM mantenimiento WHERE id_mantenimiento=?", this::mapear, id);
    }

    @Override
    public List<Mantenimiento> listarTodos(){return consultar("SELECT * FROM mantenimiento ORDER BY fecha DESC", this::mapear);}

    @Override
    public List<Mantenimiento> listarPorVehiculo(String placa){
        return consultar("SELECT * FROM mantenimiento WHERE placa=? ORDER BY fecha DESC", this::mapear, placa);
    }
}
