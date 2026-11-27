
package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.EstadoMantenimiento;
import pe.edu.utp.autorenta.domain.enums.TipoMantenimiento;
import pe.edu.utp.autorenta.domain.model.Mantenimiento;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.persistence.dao.MantenimientoDAO;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.MantenimientoDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.VehiculoDAOImpl;
import pe.edu.utp.autorenta.util.FechaUtil;
import pe.edu.utp.autorenta.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MantenimientoPanel extends JPanel {

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ConexionBD conexion;
    private MantenimientoDAO mantenimientoDAO;
    private VehiculoDAO vehiculoDAO;

    private JComboBox<Vehiculo> cmbVehiculo;
    private JComboBox<TipoMantenimiento> cmbTipo;
    private JComboBox<EstadoMantenimiento> cmbEstado;

    private JTextField txtDescripcion;
    private JTextField txtFecha;
    private JTextField txtCosto;

    private JTable tablaMantenimientos;
    private DefaultTableModel modeloTabla;

    private JButton btnRegistrar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnActualizar;

    public MantenimientoPanel() {
        configurarPanel();
        inicializarPersistencia();
        inicializarComponentes();
        configurarEventos();
        cargarVehiculos();
        cargarMantenimientos();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    // Conexión con la base de datos y los DAO.
    private void inicializarPersistencia() {
        try {
            conexion = new ConexionBD();
            conexion.inicializarEsquema();

            vehiculoDAO = new VehiculoDAOImpl(conexion);
            mantenimientoDAO = new MantenimientoDAOImpl(conexion);

        } catch (Exception e) {
            mostrarError("No se pudo conectar con la base de datos: "
                    + e.getMessage());
        }
    }

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel("GESTIÓN DE MANTENIMIENTOS");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        // Formulario.
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_BLANCO);
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del mantenimiento"));

        cmbVehiculo = new JComboBox<>();
        cmbTipo = new JComboBox<>(TipoMantenimiento.values());
        cmbEstado = new JComboBox<>(EstadoMantenimiento.values());

        txtDescripcion = new JTextField();
        txtFecha = new JTextField();
        txtCosto = new JTextField();

        panelFormulario.add(new JLabel("Vehículo:"));
        panelFormulario.add(cmbVehiculo);

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(new JLabel("Fecha (dd/MM/yyyy):"));
        panelFormulario.add(txtFecha);

        panelFormulario.add(new JLabel("Costo (S/):"));
        panelFormulario.add(txtCosto);

        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(cmbTipo);

        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(ConfiguracionApp.COLOR_FONDO);

        btnRegistrar = new JButton("Registrar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnActualizar = new JButton("Actualizar");

        btnRegistrar.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        btnRegistrar.setForeground(Color.WHITE);

        btnEliminar.setBackground(ConfiguracionApp.COLOR_ERROR);
        btnEliminar.setForeground(Color.WHITE);

        btnLimpiar.setBackground(ConfiguracionApp.COLOR_SECUNDARIO);
        btnLimpiar.setForeground(Color.WHITE);

        btnActualizar.setBackground(ConfiguracionApp.COLOR_EXITO);
        btnActualizar.setForeground(Color.WHITE);

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnActualizar);

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.setBackground(ConfiguracionApp.COLOR_FONDO);

        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de mantenimientos.
        String[] columnas = {
            "ID",
            "Placa",
            "Descripción",
            "Fecha",
            "Costo (S/)",
            "Tipo",
            "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaMantenimientos = new JTable(modeloTabla);
        tablaMantenimientos.setRowHeight(28);
        tablaMantenimientos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        tablaMantenimientos.getTableHeader().setReorderingAllowed(false);

        // Ocultar el identificador sin eliminarlo del modelo.
        tablaMantenimientos.removeColumn(
                tablaMantenimientos.getColumnModel().getColumn(0));

        JScrollPane scroll = new JScrollPane(tablaMantenimientos);
        scroll.setBorder(BorderFactory.createTitledBorder(
                "Listado de mantenimientos registrados"));

        add(scroll, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnRegistrar.addActionListener(e -> registrarMantenimiento());
        btnEliminar.addActionListener(e -> eliminarMantenimiento());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnActualizar.addActionListener(e -> actualizarDatos());
    }

    // Cargar todos los vehículos desde SQLite.
    private void cargarVehiculos() {

        cmbVehiculo.removeAllItems();

        if (vehiculoDAO == null) {
            return;
        }

        try {
            List<Vehiculo> vehiculos = vehiculoDAO.listarTodos();

            for (Vehiculo vehiculo : vehiculos) {
                cmbVehiculo.addItem(vehiculo);
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los vehículos: "
                    + e.getMessage());
        }
    }

    // Cargar mantenimientos guardados en SQLite.
    private void cargarMantenimientos() {

        modeloTabla.setRowCount(0);

        if (mantenimientoDAO == null) {
            return;
        }

        try {
            List<Mantenimiento> mantenimientos =
                    mantenimientoDAO.listarTodos();

            for (Mantenimiento mantenimiento : mantenimientos) {

                modeloTabla.addRow(new Object[]{
                    mantenimiento.getIdMantenimiento(),
                    mantenimiento.getPlaca(),
                    mantenimiento.getDescripcion(),
                    mantenimiento.getFecha().format(formatoFecha),
                    String.format(Locale.US, "%.2f",
                            mantenimiento.getCosto()),
                    mantenimiento.getTipo(),
                    mantenimiento.getEstado()
                });
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los mantenimientos: "
                    + e.getMessage());
        }
    }

    // Registrar mantenimiento en la base de datos.
    private void registrarMantenimiento() {

        if (mantenimientoDAO == null || vehiculoDAO == null) {
            mostrarError("No existe conexión con la base de datos.");
            return;
        }

        Vehiculo vehiculo = (Vehiculo) cmbVehiculo.getSelectedItem();

        String descripcion = txtDescripcion.getText().trim();
        String fecha = txtFecha.getText().trim();
        String costoTexto = txtCosto.getText().trim();

        if (vehiculo == null) {
            mostrarMensaje("Seleccione un vehículo.");
            cmbVehiculo.requestFocus();
            return;
        }

        if (!Validador.esTextoValido(descripcion)) {
            mostrarMensaje("Ingrese una descripción válida.");
            txtDescripcion.requestFocus();
            return;
        }

        LocalDate fechaConvertida = FechaUtil.convertirAFecha(fecha);

        if (fechaConvertida == null) {
            mostrarMensaje(
                    "Ingrese una fecha válida en formato dd/MM/yyyy.");
            txtFecha.requestFocus();
            return;
        }

        if (!Validador.esNumeroNoNegativo(costoTexto)) {
            mostrarMensaje("Ingrese un costo válido.");
            txtCosto.requestFocus();
            return;
        }

        try {
            double costo = Double.parseDouble(
                    costoTexto.replace(",", "."));

            TipoMantenimiento tipo =
                    (TipoMantenimiento) cmbTipo.getSelectedItem();

            EstadoMantenimiento estado =
                    (EstadoMantenimiento) cmbEstado.getSelectedItem();

            Mantenimiento mantenimiento = new Mantenimiento(
                    vehiculo.getPlaca(),
                    fechaConvertida,
                    tipo,
                    estado,
                    costo,
                    descripcion
            );

            mantenimientoDAO.guardar(mantenimiento);

            cargarMantenimientos();
            limpiarCampos();

            mostrarMensaje("Mantenimiento registrado correctamente.");

        } catch (NumberFormatException e) {
            mostrarError("El costo ingresado no es válido.");

        } catch (RuntimeException e) {
            mostrarError("No se pudo registrar el mantenimiento: "
                    + e.getMessage());
        }
    }

    // Eliminar mantenimiento seleccionado de SQLite.
    private void eliminarMantenimiento() {

        if (mantenimientoDAO == null) {
            mostrarError("No existe conexión con la base de datos.");
            return;
        }

        int filaSeleccionada = tablaMantenimientos.getSelectedRow();

        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un mantenimiento de la tabla.");
            return;
        }

        int filaModelo = tablaMantenimientos.convertRowIndexToModel(
                filaSeleccionada);

        int id = (int) modeloTabla.getValueAt(filaModelo, 0);

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este mantenimiento?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                mantenimientoDAO.eliminar(id);
                cargarMantenimientos();

                mostrarMensaje("Mantenimiento eliminado correctamente.");

            } catch (RuntimeException e) {
                mostrarError("No se pudo eliminar el mantenimiento: "
                        + e.getMessage());
            }
        }
    }

    // Actualizar las listas.
    private void actualizarDatos() {
        cargarVehiculos();
        cargarMantenimientos();
        mostrarMensaje("Información actualizada correctamente.");
    }

    // Limpiar formulario.
    public void limpiarCampos() {
        if (cmbVehiculo.getItemCount() > 0) {
            cmbVehiculo.setSelectedIndex(0);
        }

        txtDescripcion.setText("");
        txtFecha.setText("");
        txtCosto.setText("");

        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);

        txtDescripcion.requestFocus();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "AutoRenta Express",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error - AutoRenta Express",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public JTable getTablaMantenimientos() {
        return tablaMantenimientos;
    }
}
