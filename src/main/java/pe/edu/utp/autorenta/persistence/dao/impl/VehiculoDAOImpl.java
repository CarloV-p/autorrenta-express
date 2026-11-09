package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.domain.enums.TipoVehiculo;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;

public class VehiculoDAOImpl extends BaseDAO implements VehiculoDAO {

    public VehiculoDAOImpl(ConexionBD conexion){super(conexion);}

    private Vehiculo mapear(ResultSet rs) throws SQLException{
        return new Vehiculo(rs.getString("placa"), rs.getString("marca"), rs.getString("modelo"),
                TipoVehiculo.valueOf(rs.getString("tipo")), EstadoVehiculo.valueOf(rs.getString("estado")),
                rs.getDouble("kilometraje"), rs.getDouble("precio_base_por_dia"),
                rs.getDouble("km_proximo_mantenimiento"));
    }

    @Override
    public Vehiculo guardar(Vehiculo v){
        ejecutar("INSERT INTO vehiculo (placa, marca, modelo, tipo, estado, kilometraje, "
                        + "precio_base_por_dia, km_proximo_mantenimiento) VALUES (?,?,?,?,?,?,?,?)",
                v.getPlaca(), v.getMarca(), v.getModelo(), v.getTipo(), v.getEstado(),
                v.getKilometraje(), v.getPrecioBasePorDia(), v.getKilometrajeProximoMantenimiento());
        return v;
    }

    @Override
    public void actualizar(Vehiculo v){
        ejecutar("UPDATE vehiculo SET marca=?, modelo=?, tipo=?, estado=?, kilometraje=?, "
                        + "precio_base_por_dia=?, km_proximo_mantenimiento=? WHERE placa=?",
                v.getMarca(), v.getModelo(), v.getTipo(), v.getEstado(), v.getKilometraje(),
                v.getPrecioBasePorDia(), v.getKilometrajeProximoMantenimiento(), v.getPlaca());
    }

    @Override
    public void eliminar(String placa){ejecutar("DELETE FROM vehiculo WHERE placa=?", placa);}

    @Override
    public Optional<Vehiculo> buscarPorId(String placa){
        return consultarUno("SELECT * FROM vehiculo WHERE placa=?", this::mapear, placa.toUpperCase());
    }

    @Override
    public List<Vehiculo> listarTodos(){return consultar("SELECT * FROM vehiculo ORDER BY placa", this::mapear);}

    @Override
    public List<Vehiculo> listarPorEstado(EstadoVehiculo estado){
        return consultar("SELECT * FROM vehiculo WHERE estado=? ORDER BY placa", this::mapear, estado);
    }

    @Override
    public List<Vehiculo> listarPorTipo(TipoVehiculo tipo){
        return consultar("SELECT * FROM vehiculo WHERE tipo=? ORDER BY placa", this::mapear, tipo);
    }

    @Override
    public List<Vehiculo> listarDisponiblesEntre(LocalDate inicio, LocalDate fin){
        // Rangos se cruzan si inicio_existente <= fin_pedido Y fin_existente >= inicio_pedido
        return consultar("SELECT * FROM vehiculo v WHERE v.estado='DISPONIBLE' AND NOT EXISTS ("
                + "SELECT 1 FROM contrato c WHERE c.placa=v.placa AND c.estado_contrato='ACTIVO' "
                + "AND c.fecha_inicio <= ? AND c.fecha_fin >= ?) ORDER BY v.placa",
                this::mapear, fin, inicio);
    }
}
