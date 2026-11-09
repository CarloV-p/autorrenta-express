package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.model.AlertaMantenimiento;
import pe.edu.utp.autorenta.persistence.dao.AlertaMantenimientoDAO;

public class AlertaMantenimientoDAOImpl extends BaseDAO implements AlertaMantenimientoDAO {
    public AlertaMantenimientoDAOImpl(ConexionBD conexion){super(conexion);}

    private AlertaMantenimiento mapear(ResultSet rs) throws SQLException{
        return new AlertaMantenimiento(rs.getInt("id_alerta"), rs.getString("placa"), rs.getDouble("km_actual"),
                rs.getDouble("km_limite"), LocalDateTime.parse(rs.getString("fecha_generacion")),
                rs.getString("mensaje"), rs.getInt("atendida") == 1);
    }

    @Override
    public AlertaMantenimiento registrar(AlertaMantenimiento a){
        a.setIdAlerta(insertar("INSERT INTO alerta_mantenimiento (placa, km_actual, km_limite, "
                        + "fecha_generacion, mensaje, atendida) VALUES (?,?,?,?,?,?)",
                a.getPlaca(), a.getKilometrajeActual(), a.getKilometrajeLimite(),
                a.getFechaGeneracion(), a.getMensaje(), a.isAtendida()));
        return a;
    }

    @Override
    public List<AlertaMantenimiento> listarPendientes(){
        return consultar("SELECT * FROM alerta_mantenimiento WHERE atendida=0 ORDER BY fecha_generacion DESC", this::mapear);
    }

    @Override
    public List<AlertaMantenimiento> listarPorVehiculo(String placa){
        return consultar("SELECT * FROM alerta_mantenimiento WHERE placa=? ORDER BY fecha_generacion DESC", this::mapear, placa);
    }

    @Override
    public void marcarAtendida(int idAlerta){
        ejecutar("UPDATE alerta_mantenimiento SET atendida=1 WHERE id_alerta=?", idAlerta);
    }
}
