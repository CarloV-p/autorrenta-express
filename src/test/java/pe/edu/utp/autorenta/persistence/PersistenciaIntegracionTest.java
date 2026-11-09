package pe.edu.utp.autorenta.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoContrato;
import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.enums.TipoContrato;
import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;
import pe.edu.utp.autorenta.domain.exception.VehiculoNoDisponibleException;
import pe.edu.utp.autorenta.domain.model.*;
import pe.edu.utp.autorenta.persistence.dao.*;
import pe.edu.utp.autorenta.persistence.dao.impl.*;

/** Prueba de integración de la capa de persistencia sobre una BD SQLite temporal. */
class PersistenciaIntegracionTest {

    private ClienteDAO clienteDAO;
    private VehiculoDAO vehiculoDAO;
    private ConductorDAO conductorDAO;
    private CoberturaDAO coberturaDAO;
    private ContratoDAO contratoDAO;
    private AlertaMantenimientoDAO alertaDAO;
    private UsuarioDAO usuarioDAO;

    @BeforeEach
    void preparar() throws IOException {
        Path archivo = Files.createTempFile("autorenta-test", ".db");
        Files.deleteIfExists(archivo);
        ConexionBD bd = new ConexionBD("jdbc:sqlite:" + archivo);
        bd.inicializarEsquema();
        clienteDAO = new ClienteDAOImpl(bd);
        vehiculoDAO = new VehiculoDAOImpl(bd);
        conductorDAO = new ConductorDAOImpl(bd);
        coberturaDAO = new CoberturaDAOImpl(bd);
        contratoDAO = new ContratoDAOImpl(bd, clienteDAO, vehiculoDAO, conductorDAO, coberturaDAO);
        alertaDAO = new AlertaMantenimientoDAOImpl(bd);
        usuarioDAO = new UsuarioDAOImpl(bd);
    }

    @Test
    void esquemaCargaDatosIniciales() {
        assertEquals(4, vehiculoDAO.listarTodos().size());
        assertEquals(4, coberturaDAO.listarTodos().size());
        assertTrue(usuarioDAO.buscarPorNombreUsuario("admin").isPresent());
    }

    @Test
    void contratoSimpleSeGuardaYSeRecupera() {
        Cliente cliente = clienteDAO.buscarPorDni("12345678").orElseThrow();
        Vehiculo auto = vehiculoDAO.buscarPorId("ABC-123").orElseThrow();
        ContratoAlquiler c = new ContratoAlquiler(cliente, auto, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15));
        c.agregarConductor(conductorDAO.buscarPorDni("87654321").orElseThrow());
        c.agregarCobertura(coberturaDAO.buscarPorTipo(TipoCobertura.GPS).orElseThrow());
        c.setMontoTotal(500);
        contratoDAO.guardar(c);

        assertTrue(c.getIdContrato() > 0);
        ContratoAlquiler leido = contratoDAO.buscarPorId(c.getIdContrato()).orElseThrow();
        assertEquals(TipoContrato.SIMPLE, leido.getTipoContrato());
        assertEquals(5, leido.getDiasAlquiler());
        assertEquals(1, leido.getConductores().size());
        assertEquals(1, leido.getCoberturas().size());
        assertTrue(contratoDAO.existeContratoActivoEnRango("ABC-123", LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 20)));
        assertFalse(contratoDAO.existeContratoActivoEnRango("ABC-123", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5)));
    }

    @Test
    void contratoCorporativoGuardaTramos() {
        Cliente cliente = clienteDAO.buscarPorDni("12345678").orElseThrow();
        Vehiculo suv = vehiculoDAO.buscarPorId("DEF-456").orElseThrow();
        ContratoCorporativo cc = new ContratoCorporativo(cliente, suv, LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 12, 10), "Minera Andes S.A.C.");
        cc.setLimiteKilometrajeMensual(3000);
        cc.agregarTramo(new TramoContrato(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 20), "Arequipa"));
        cc.agregarTramo(new TramoContrato(LocalDate.of(2026, 11, 21), LocalDate.of(2026, 12, 10), "Cusco"));
        contratoDAO.guardar(cc);

        ContratoAlquiler leido = contratoDAO.buscarPorId(cc.getIdContrato()).orElseThrow();
        assertTrue(leido instanceof ContratoCorporativo);
        assertEquals(2, ((ContratoCorporativo) leido).getTramos().size());
        assertEquals(TipoContrato.CORPORATIVO, leido.getTipoContrato());
    }

    @Test
    void disponibilidadExcluyeVehiculosConContratoActivo() {
        Cliente cliente = clienteDAO.buscarPorDni("12345678").orElseThrow();
        Vehiculo auto = vehiculoDAO.buscarPorId("ABC-123").orElseThrow();
        contratoDAO.guardar(new ContratoAlquiler(cliente, auto, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15)));
        var libres = vehiculoDAO.listarDisponiblesEntre(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14));
        assertEquals(3, libres.size());
    }

    @Test
    void observerPuedeRegistrarAlertaPorKilometraje() {
        Vehiculo v = vehiculoDAO.buscarPorId("ABC-123").orElseThrow();
        assertTrue(v.registrarKilometraje(20500));
        vehiculoDAO.actualizar(v);
        alertaDAO.registrar(AlertaMantenimiento.para(v));
        assertEquals(1, alertaDAO.listarPendientes().size());
        alertaDAO.marcarAtendida(alertaDAO.listarPendientes().get(0).getIdAlerta());
        assertEquals(0, alertaDAO.listarPendientes().size());
    }

    @Test
    void reglasDeDominio() {
        Vehiculo v = vehiculoDAO.buscarPorId("ABC-123").orElseThrow();
        v.marcarAlquilado();
        assertEquals(EstadoVehiculo.ALQUILADO, v.getEstado());
        assertThrows(VehiculoNoDisponibleException.class, v::marcarAlquilado);
        assertThrows(DatosInvalidosException.class, () -> new Cliente("Ana", "123", "999"));
        assertThrows(DatosInvalidosException.class, () -> v.registrarKilometraje(1));
        ContratoAlquiler c = new ContratoAlquiler(clienteDAO.buscarPorDni("12345678").orElseThrow(), v,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 3));
        c.cancelar();
        assertEquals(EstadoContrato.CANCELADO, c.getEstadoContrato());
        assertNotNull(c.getVehiculo());
    }
}
