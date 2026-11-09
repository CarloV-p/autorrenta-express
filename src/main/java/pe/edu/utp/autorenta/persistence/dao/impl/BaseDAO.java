package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.exception.PersistenciaException;

/*
    plantilla con el código JDBC repetitivo (abrir, preparar, cerrar, traducir errores)
    cada DAOImpl solo escribe su SQL y cómo convertir una fila en objeto
    convierte toda SQLException en PersistenciaException
 */
abstract class BaseDAO {
    protected final ConexionBD conexion;

    protected BaseDAO(ConexionBD conexion){this.conexion = conexion;}

    // convierte una fila del ResultSet en un objeto del dominio
    @FunctionalInterface
    protected interface MapeadorFila<T>{
        T mapear(ResultSet rs) throws SQLException;
    }

    // bloque de trabajo que se ejecuta dentro de una transacción
    @FunctionalInterface
    protected interface Transaccion<R>{
        R ejecutar(Connection c) throws SQLException;
    }

    protected <T> List<T> consultar(String sql, MapeadorFila<T> mapeador, Object... params){
        try(Connection c = conexion.obtenerConexion()){
            return consultar(c, sql, mapeador, params);
        }catch(SQLException e){
            throw new PersistenciaException("Error al consultar: " + sql, e);
        }
    }

    protected <T> List<T> consultar(Connection c, String sql, MapeadorFila<T> mapeador, Object... params)
            throws SQLException{
        try(PreparedStatement ps = c.prepareStatement(sql)){
            asignar(ps, params);
            try(ResultSet rs = ps.executeQuery()){
                List<T> lista = new ArrayList<>();
                while(rs.next()){
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        }
    }

    protected <T> Optional<T> consultarUno(String sql, MapeadorFila<T> mapeador, Object... params){
        List<T> lista = consultar(sql, mapeador, params);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    // INSERT, UPDATE y DELETE devuelve filas afectadas
    protected int ejecutar(String sql, Object... params){
        try(Connection c = conexion.obtenerConexion()){
            return ejecutar(c, sql, params);
        }catch(SQLException e){
            throw new PersistenciaException("Error al ejecutar: " + sql, e);
        }
    }

    protected int ejecutar(Connection c, String sql, Object... params) throws SQLException{
        try(PreparedStatement ps = c.prepareStatement(sql)){
            asignar(ps, params);
            return ps.executeUpdate();
        }
    }

    // INSERT que devuelve el id autogenerado
    protected int insertar(String sql, Object... params){
        try(Connection c = conexion.obtenerConexion()){
            return insertar(c, sql, params);
        }catch(SQLException e){
            throw new PersistenciaException("Error al insertar: " + sql, e);
        }
    }

    protected int insertar(Connection c, String sql, Object... params) throws SQLException{
        try(PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            asignar(ps, params);
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    // ejecuta varias operaciones de forma atomica, o se guardan todas o ninguna (rollback)
    protected <R> R enTransaccion(Transaccion<R> trabajo){
        try(Connection c = conexion.obtenerConexion()){
            c.setAutoCommit(false);
            try{
                R resultado = trabajo.ejecutar(c);
                c.commit();
                return resultado;
            }catch(SQLException | RuntimeException e){
                c.rollback();
                throw e;
            }
        }catch(SQLException e){
            throw new PersistenciaException("Error en la transacción.", e);
        }
    }

    private void asignar(PreparedStatement ps, Object[] params) throws SQLException{
        for(int i = 0; i < params.length; i++){
            Object p = params[i];
            int idx = i + 1;
            if(p == null){
                ps.setObject(idx, null);
            }else if(p instanceof Enum<?> e){
                ps.setString(idx, e.name());
            }else if(p instanceof LocalDate || p instanceof LocalDateTime){
                ps.setString(idx, p.toString());
            }else if(p instanceof Boolean b){
                ps.setInt(idx, b ? 1 : 0);
            }else{
                ps.setObject(idx, p);
            }
        }
    }

    // lectura segura de columnas opcionales
    protected static LocalDate fecha(ResultSet rs, String col) throws SQLException{
        String s = rs.getString(col);
        return s == null ? null : LocalDate.parse(s);
    }

    protected static Double doubleONull(ResultSet rs, String col) throws SQLException{
        double d = rs.getDouble(col);
        return rs.wasNull() ? null : d;
    }
}
