
package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReportePanel extends JPanel {

    private final ConexionBD conexion;

    private JComboBox<String> cmbReporte;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblVehiculos;
    private JLabel lblClientes;
    private JLabel lblContratos;
    private JLabel lblMantenimientos;
    private JLabel lblIngresos;

    public ReportePanel() {

        conexion = new ConexionBD();

        try {
            conexion.inicializarEsquema();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo inicializar la base de datos: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        inicializarComponentes();
        actualizarReportes();
    }

    private void inicializarComponentes() {

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("REPORTES DEL SISTEMA");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(new Color(35, 65, 90));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.add(titulo, BorderLayout.WEST);

        JButton btnActualizar = new JButton("Actualizar reportes");
        btnActualizar.addActionListener(e -> actualizarReportes());
        panelSuperior.add(btnActualizar, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // RESUMEN GENERAL
        JPanel panelResumen = new JPanel(new GridLayout(1, 5, 10, 10));
        panelResumen.setOpaque(false);

        lblVehiculos = crearTarjeta(panelResumen, "Vehículos");
        lblClientes = crearTarjeta(panelResumen, "Clientes");
        lblContratos = crearTarjeta(panelResumen, "Contratos");
        lblMantenimientos = crearTarjeta(panelResumen, "Mantenimientos");
        lblIngresos = crearTarjeta(panelResumen, "Ingresos registrados");

        // SELECTOR DE REPORTES
        cmbReporte = new JComboBox<>(new String[]{
            "Vehículos",
            "Clientes",
            "Contratos",
            "Mantenimientos",
            "Pagos",
            "Coberturas"
        });

        cmbReporte.addActionListener(e -> cargarReporteSeleccionado());

        JPanel panelSelector = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSelector.setOpaque(false);
        panelSelector.add(new JLabel("Seleccionar reporte:"));
        panelSelector.add(cmbReporte);

        JPanel panelCentroSuperior = new JPanel(new BorderLayout(10, 10));
        panelCentroSuperior.setOpaque(false);
        panelCentroSuperior.add(panelResumen, BorderLayout.NORTH);
        panelCentroSuperior.add(panelSelector, BorderLayout.SOUTH);

        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Información del reporte"));

        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setOpaque(false);
        panelContenido.add(panelCentroSuperior, BorderLayout.NORTH);
        panelContenido.add(scroll, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }

    private JLabel crearTarjeta(JPanel panel, String titulo) {

        JPanel tarjeta = new JPanel(new BorderLayout(5, 10));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 230)),
                BorderFactory.createEmptyBorder(12, 8, 12, 8)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValor = new JLabel("0", SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 19));
        lblValor.setForeground(new Color(35, 90, 130));

        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);

        panel.add(tarjeta);

        return lblValor;
    }

    private void actualizarReportes() {

        try (Connection conn = conexion.obtenerConexion()) {

            lblVehiculos.setText(String.valueOf(
                    consultarTotal(conn, "SELECT COUNT(*) FROM vehiculo")
            ));

            lblClientes.setText(String.valueOf(
                    consultarTotal(conn, "SELECT COUNT(*) FROM cliente")
            ));

            lblContratos.setText(String.valueOf(
                    consultarTotal(conn, "SELECT COUNT(*) FROM contrato")
            ));

            lblMantenimientos.setText(String.valueOf(
                    consultarTotal(conn, "SELECT COUNT(*) FROM mantenimiento")
            ));

            double ingresos = consultarDecimal(
                    conn,
                    "SELECT COALESCE(SUM(monto), 0) FROM pago"
            );

            lblIngresos.setText(String.format("S/ %.2f", ingresos));

            cargarReporteSeleccionado();

        } catch (SQLException e) {
            mostrarError("No se pudieron actualizar los reportes.", e);
        }
    }

    private int consultarTotal(Connection conn, String sql)
            throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private double consultarDecimal(Connection conn, String sql)
            throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    private void cargarReporteSeleccionado() {

        if (cmbReporte == null || modeloTabla == null) {
            return;
        }

        String opcion = (String) cmbReporte.getSelectedItem();

        if (opcion == null) {
            return;
        }

        String sql;

        switch (opcion) {

            case "Vehículos":
                sql = "SELECT placa AS 'Placa', marca AS 'Marca', "
                        + "modelo AS 'Modelo', tipo AS 'Tipo', "
                        + "estado AS 'Estado', kilometraje AS 'Kilometraje', "
                        + "precio_base_por_dia AS 'Precio por día' "
                        + "FROM vehiculo ORDER BY placa";
                break;

            case "Clientes":
                sql = "SELECT id_cliente AS 'Código', nombre AS 'Nombre', "
                        + "dni AS 'DNI', telefono AS 'Teléfono' "
                        + "FROM cliente ORDER BY id_cliente";
                break;

            case "Contratos":
                sql = "SELECT c.id_contrato AS 'Código', "
                        + "cl.nombre AS 'Cliente', "
                        + "v.placa AS 'Placa', "
                        + "v.marca AS 'Marca', "
                        + "v.modelo AS 'Modelo', "
                        + "c.fecha_inicio AS 'Fecha inicio', "
                        + "c.fecha_fin AS 'Fecha fin', "
                        + "c.tipo_contrato AS 'Tipo', "
                        + "c.estado_contrato AS 'Estado', "
                        + "c.monto_total AS 'Monto total' "
                        + "FROM contrato c "
                        + "INNER JOIN cliente cl "
                        + "ON c.id_cliente = cl.id_cliente "
                        + "INNER JOIN vehiculo v "
                        + "ON c.placa = v.placa "
                        + "ORDER BY c.id_contrato DESC";
                break;

            case "Mantenimientos":
                sql = "SELECT m.id_mantenimiento AS 'Código', "
                        + "m.placa AS 'Placa', "
                        + "m.fecha AS 'Fecha', "
                        + "m.tipo AS 'Tipo', "
                        + "m.estado AS 'Estado', "
                        + "m.costo AS 'Costo', "
                        + "m.descripcion AS 'Descripción' "
                        + "FROM mantenimiento m "
                        + "ORDER BY m.id_mantenimiento DESC";
                break;

            case "Pagos":
                sql = "SELECT p.id_pago AS 'Código', "
                        + "p.id_contrato AS 'Contrato', "
                        + "cl.nombre AS 'Cliente', "
                        + "p.fecha AS 'Fecha', "
                        + "p.monto AS 'Monto', "
                        + "p.metodo AS 'Método de pago' "
                        + "FROM pago p "
                        + "INNER JOIN contrato c "
                        + "ON p.id_contrato = c.id_contrato "
                        + "INNER JOIN cliente cl "
                        + "ON c.id_cliente = cl.id_cliente "
                        + "ORDER BY p.id_pago DESC";
                break;

            case "Coberturas":
                sql = "SELECT id_cobertura AS 'Código', "
                        + "tipo AS 'Tipo', "
                        + "nombre AS 'Nombre', "
                        + "costo_por_dia AS 'Costo por día' "
                        + "FROM cobertura ORDER BY id_cobertura";
                break;

            default:
                return;
        }

        try (Connection conn = conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnas = meta.getColumnCount();

            modeloTabla.setRowCount(0);
            modeloTabla.setColumnCount(0);

            for (int i = 1; i <= columnas; i++) {
                modeloTabla.addColumn(meta.getColumnLabel(i));
            }

            while (rs.next()) {

                Object[] fila = new Object[columnas];

                for (int i = 1; i <= columnas; i++) {
                    fila[i - 1] = rs.getObject(i);
                }

                modeloTabla.addRow(fila);
            }

        } catch (SQLException e) {
            mostrarError("No se pudo cargar el reporte de " + opcion + ".", e);
        }
    }

    private void mostrarError(String mensaje, Exception e) {

        JOptionPane.showMessageDialog(
                this,
                mensaje + "\nDetalle: " + e.getMessage(),
                "Error en reportes",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
