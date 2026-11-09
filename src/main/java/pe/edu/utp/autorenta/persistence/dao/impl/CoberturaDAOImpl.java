package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;
import pe.edu.utp.autorenta.persistence.dao.CoberturaDAO;

public class CoberturaDAOImpl extends BaseDAO implements CoberturaDAO {
    public CoberturaDAOImpl(ConexionBD conexion){super(conexion);}

    private CoberturaSeguro mapear(ResultSet rs) throws SQLException{
        return new CoberturaSeguro(rs.getInt("id_cobertura"), TipoCobertura.valueOf(rs.getString("tipo")),
                rs.getString("nombre"), rs.getDouble("costo_por_dia"));
    }

    @Override
    public CoberturaSeguro guardar(CoberturaSeguro c){
        c.setIdCobertura(insertar("INSERT INTO cobertura (tipo, nombre, costo_por_dia) VALUES (?,?,?)",
                c.getTipo(), c.getNombre(), c.getCostoPorDia()));
        return c;
    }

    @Override
    public void actualizar(CoberturaSeguro c){
        ejecutar("UPDATE cobertura SET nombre=?, costo_por_dia=? WHERE id_cobertura=?",
                c.getNombre(), c.getCostoPorDia(), c.getIdCobertura());
    }

    @Override
    public void eliminar(Integer id){ejecutar("DELETE FROM cobertura WHERE id_cobertura=?", id);}

    @Override
    public Optional<CoberturaSeguro> buscarPorId(Integer id){
        return consultarUno("SELECT * FROM cobertura WHERE id_cobertura=?", this::mapear, id);
    }

    @Override
    public List<CoberturaSeguro> listarTodos(){return consultar("SELECT * FROM cobertura ORDER BY id_cobertura", this::mapear);}

    @Override
    public Optional<CoberturaSeguro> buscarPorTipo(TipoCobertura tipo){
        return consultarUno("SELECT * FROM cobertura WHERE tipo=?", this::mapear, tipo);
    }
}
