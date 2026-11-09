package pe.edu.utp.autorenta.persistence.dao.impl;
// @author Valdivia Paricahua Carlo Alexander

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pe.edu.utp.autorenta.config.ConexionBD;

import pe.edu.utp.autorenta.domain.enums.EstadoContrato;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.enums.TipoContrato;

import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.exception.PersistenciaException;

import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;
import pe.edu.utp.autorenta.domain.model.Conductor;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.ContratoCorporativo;
import pe.edu.utp.autorenta.domain.model.TramoContrato;

import pe.edu.utp.autorenta.persistence.dao.ClienteDAO;
import pe.edu.utp.autorenta.persistence.dao.CoberturaDAO;
import pe.edu.utp.autorenta.persistence.dao.ConductorDAO;
import pe.edu.utp.autorenta.persistence.dao.ContratoDAO;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;

/*
    DAO mas complejo, un contrato se reparte en varias tablas 
    (contrato, contrato_conductor, contrato_cobertura, tramo_contrato)
    depende de las INTERFACES de los demas DAO (DIP: Principio de inversion de dependencias)
 */
public class ContratoDAOImpl extends BaseDAO implements ContratoDAO {
    private final ClienteDAO clienteDAO;
    private final VehiculoDAO vehiculoDAO;
    private final ConductorDAO conductorDAO;
    private final CoberturaDAO coberturaDAO;

    public ContratoDAOImpl(ConexionBD conexion, ClienteDAO clienteDAO, VehiculoDAO vehiculoDAO,
                           ConductorDAO conductorDAO, CoberturaDAO coberturaDAO){
        super(conexion);
        this.clienteDAO = clienteDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.conductorDAO = conductorDAO;
        this.coberturaDAO = coberturaDAO;
    }

    // fila cruda del contrato, se lee primero y se contruye el objeto con los datos despues de cerrar el ResultSet
    private record Fila(int id, int idCliente, String placa, LocalDate inicio, LocalDate fin,
                        TipoContrato tipo, TipoCobro cobro, EstadoContrato estado, double monto,
                        double deposito, double kmPactado, double kmInicial, Double kmFinal,
                        String razonSocial, String ruc, Double limiteKm){}

    private Fila mapearFila(ResultSet rs) throws SQLException{
        return new Fila(rs.getInt("id_contrato"), rs.getInt("id_cliente"), rs.getString("placa"),
                fecha(rs, "fecha_inicio"), fecha(rs, "fecha_fin"),
                TipoContrato.valueOf(rs.getString("tipo_contrato")), TipoCobro.valueOf(rs.getString("tipo_cobro")),
                EstadoContrato.valueOf(rs.getString("estado_contrato")), rs.getDouble("monto_total"),
                rs.getDouble("deposito_garantia"), rs.getDouble("km_pactado"), rs.getDouble("km_inicial"),
                doubleONull(rs, "km_final"), rs.getString("razon_social"), rs.getString("ruc"),
                doubleONull(rs, "limite_km_mensual"));
    }

    private List<ContratoAlquiler> consultarContratos(String sql, Object... params){
        List<Fila> filas = consultar(sql, this::mapearFila, params);
        List<ContratoAlquiler> resultado = new ArrayList<>();
        for(Fila f : filas){
            resultado.add(hidratar(f));
        }
        return resultado;
    }

    private ContratoAlquiler hidratar(Fila f){
        var cliente = clienteDAO.buscarPorId(f.idCliente()).orElseThrow(() ->
                new PersistenciaException("Cliente " + f.idCliente() + " no existe (contrato " + f.id() + ").", null));
        var vehiculo = vehiculoDAO.buscarPorId(f.placa()).orElseThrow(() ->
                new PersistenciaException("Vehículo " + f.placa() + " no existe (contrato " + f.id() + ").", null));

        ContratoAlquiler c;
        if (f.tipo() == TipoContrato.CORPORATIVO){
            ContratoCorporativo cc = new ContratoCorporativo(cliente, vehiculo, f.inicio(), f.fin(),
                    f.razonSocial() == null ? "SIN RAZÓN SOCIAL" : f.razonSocial());
            cc.setRuc(f.ruc());
            if(f.limiteKm() != null){
                cc.setLimiteKilometrajeMensual(f.limiteKm());
            }
            consultar("SELECT * FROM tramo_contrato WHERE id_contrato=? ORDER BY fecha_inicio", rs ->
                    new TramoContrato(rs.getInt("id_tramo"), fecha(rs, "fecha_inicio"),
                            fecha(rs, "fecha_fin"), rs.getString("destino")), f.id()).forEach(cc::agregarTramo);
            c = cc;
        }else{
            c = new ContratoAlquiler(cliente, vehiculo, f.inicio(), f.fin());
        }
        c.setIdContrato(f.id());
        c.setEstadoContrato(f.estado());
        c.setTipoCobro(f.cobro());
        c.setMontoTotal(f.monto());
        c.setDepositoGarantia(f.deposito());
        c.setKilometrajePactado(f.kmPactado());
        c.setKilometrajeInicial(f.kmInicial());
        c.setKilometrajeFinal(f.kmFinal());

        for(Integer idCond : consultar("SELECT id_conductor FROM contrato_conductor WHERE id_contrato=?",
                rs -> rs.getInt(1), f.id())){
            conductorDAO.buscarPorId(idCond).ifPresent(c::agregarConductor);
        }
        for(Integer idCob : consultar("SELECT id_cobertura FROM contrato_cobertura WHERE id_contrato=?",
                rs -> rs.getInt(1), f.id())){
            coberturaDAO.buscarPorId(idCob).ifPresent(c::agregarCobertura);
        }
        return c;
    }

    // escritura
    private static final String SQL_INSERT = "INSERT INTO contrato (id_cliente, placa, fecha_inicio, fecha_fin, "
            + "tipo_contrato, tipo_cobro, estado_contrato, monto_total, deposito_garantia, km_pactado, km_inicial, "
            + "km_final, razon_social, ruc, limite_km_mensual) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private Object[] parametros(ContratoAlquiler c){
        String razon = null;
        String ruc = null;
        Double limite = null;
        if(c instanceof ContratoCorporativo cc){
            razon = cc.getRazonSocialEmpresa();
            ruc = cc.getRuc();
            limite = cc.getLimiteKilometrajeMensual();
        }
        return new Object[]{c.getCliente().getIdCliente(), c.getVehiculo().getPlaca(), c.getFechaInicio(),
            c.getFechaFin(), c.getTipoContrato(), c.getTipoCobro(), c.getEstadoContrato(), c.getMontoTotal(),
            c.getDepositoGarantia(), c.getKilometrajePactado(), c.getKilometrajeInicial(), c.getKilometrajeFinal(),
            razon, ruc, limite};
    }

    private void guardarRelaciones(Connection cn, ContratoAlquiler c) throws SQLException{
        for(Conductor cond : c.getConductores()){
            if(cond.getIdConductor() <= 0){
                throw new DatosInvalidosException("El conductor " + cond.getNombre() + " debe guardarse antes del contrato.");
            }
            ejecutar(cn, "INSERT INTO contrato_conductor (id_contrato, id_conductor) VALUES (?,?)",
                    c.getIdContrato(), cond.getIdConductor());
        }
        for(CoberturaSeguro cob : c.getCoberturas()){
            if(cob.getIdCobertura() <= 0){
                throw new DatosInvalidosException("La cobertura " + cob.getNombre() + " debe provenir del catálogo.");
            }
            ejecutar(cn, "INSERT INTO contrato_cobertura (id_contrato, id_cobertura) VALUES (?,?)",
                    c.getIdContrato(), cob.getIdCobertura());
        }
        if(c instanceof ContratoCorporativo cc){
            for(TramoContrato t : cc.getTramos()){
                t.setIdTramo(insertar(cn, "INSERT INTO tramo_contrato (id_contrato, fecha_inicio, fecha_fin, destino) "
                        + "VALUES (?,?,?,?)", c.getIdContrato(), t.getFechaInicioTramo(), t.getFechaFinTramo(), t.getDestino()));
            }
        }
    }

    @Override
    public ContratoAlquiler guardar(ContratoAlquiler c){
        if(c.getCliente().getIdCliente() <= 0){
            throw new DatosInvalidosException("El cliente debe guardarse antes que el contrato.");
        }
        return enTransaccion(cn -> {
            c.setIdContrato(insertar(cn, SQL_INSERT, parametros(c)));
            guardarRelaciones(cn, c);
            return c;
        });
    }

    @Override
    public void actualizar(ContratoAlquiler c){
        enTransaccion(cn -> {
            ejecutar(cn, "UPDATE contrato SET id_cliente=?, placa=?, fecha_inicio=?, fecha_fin=?, tipo_contrato=?, "
                    + "tipo_cobro=?, estado_contrato=?, monto_total=?, deposito_garantia=?, km_pactado=?, km_inicial=?, "
                    + "km_final=?, razon_social=?, ruc=?, limite_km_mensual=? WHERE id_contrato=?",
                    concatenar(parametros(c), c.getIdContrato()));
            ejecutar(cn, "DELETE FROM contrato_conductor WHERE id_contrato=?", c.getIdContrato());
            ejecutar(cn, "DELETE FROM contrato_cobertura WHERE id_contrato=?", c.getIdContrato());
            ejecutar(cn, "DELETE FROM tramo_contrato WHERE id_contrato=?", c.getIdContrato());
            guardarRelaciones(cn, c);
            return null;
        });
    }

    private static Object[] concatenar(Object[] base, Object extra){
        Object[] r = java.util.Arrays.copyOf(base, base.length + 1);
        r[base.length] = extra;
        return r;
    }

    @Override
    public void eliminar(Integer id){
        // las tablas hijas se borran solas (ON DELETE CASCADE)
        // fallara si hay pagos asociados
        ejecutar("DELETE FROM contrato WHERE id_contrato=?", id);
    }

    @Override
    public void actualizarEstado(int idContrato, EstadoContrato nuevoEstado){
        ejecutar("UPDATE contrato SET estado_contrato=? WHERE id_contrato=?", nuevoEstado, idContrato);
    }

    // lectura
    @Override
    public Optional<ContratoAlquiler> buscarPorId(Integer id){
        List<ContratoAlquiler> l = consultarContratos("SELECT * FROM contrato WHERE id_contrato=?", id);
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(0));
    }
    @Override
    public List<ContratoAlquiler> listarTodos(){
        return consultarContratos("SELECT * FROM contrato ORDER BY id_contrato DESC");
    }
    @Override
    public List<ContratoAlquiler> listarPorCliente(int idCliente){
        return consultarContratos("SELECT * FROM contrato WHERE id_cliente=? ORDER BY id_contrato DESC", idCliente);
    }
    @Override
    public List<ContratoAlquiler> listarPorVehiculo(String placa){
        return consultarContratos("SELECT * FROM contrato WHERE placa=? ORDER BY id_contrato DESC", placa);
    }
    @Override
    public List<ContratoAlquiler> listarPorEstado(EstadoContrato estado){
        return consultarContratos("SELECT * FROM contrato WHERE estado_contrato=? ORDER BY id_contrato DESC", estado);
    }
    @Override
    public boolean existeContratoActivoEnRango(String placa, LocalDate inicio, LocalDate fin){
        return !consultar("SELECT 1 FROM contrato WHERE placa=? AND estado_contrato='ACTIVO' "
                + "AND fecha_inicio <= ? AND fecha_fin >= ? LIMIT 1", rs -> rs.getInt(1), placa, fin, inicio).isEmpty();
    }
}
