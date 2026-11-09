package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.MetodoPago;
import pe.edu.utp.autorenta.domain.model.Pago;
import pe.edu.utp.autorenta.persistence.dao.PagoDAO;

public class PagoDAOImpl extends BaseDAO implements PagoDAO {

    public PagoDAOImpl(ConexionBD conexion){super(conexion);}

    private Pago mapear(ResultSet rs) throws SQLException{
        return new Pago(rs.getInt("id_pago"), rs.getInt("id_contrato"), fecha(rs, "fecha"),
                rs.getDouble("monto"), MetodoPago.valueOf(rs.getString("metodo")));
    }

    @Override
    public Pago registrar(Pago p){
        p.setIdPago(insertar("INSERT INTO pago (id_contrato, fecha, monto, metodo) VALUES (?,?,?,?)",
                p.getIdContrato(), p.getFecha(), p.getMonto(), p.getMetodo()));
        return p;
    }

    @Override
    public List<Pago> listarPorContrato(int idContrato){
        return consultar("SELECT * FROM pago WHERE id_contrato=? ORDER BY fecha", this::mapear, idContrato);
    }
}
