
package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.TipoVehiculo;
import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.VehiculoDAOImpl;
import pe.edu.utp.autorenta.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VehiculoPanel extends JPanel {

    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtKilometraje;
    private JTextField txtPrecio;
    private JTextField txtProximoMantenimiento;

    private JComboBox<TipoVehiculo> cmbTipo;
    private JComboBox<EstadoVehiculo> cmbEstado;

    private JTable tablaVehiculos;
    private DefaultTableModel modeloTabla;

    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final VehiculoDAO vehiculoDAO;

    public VehiculoPanel() {
        this.vehiculoDAO = new VehiculoDAOImpl(new ConexionBD());

        configurarPanel();
        inicializarComponentes();
        cargarVehiculos();
    }

    // Configuración general del panel.
    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);

        setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15
        ));
    }

    // Inicialización de los componentes.
    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel("GESTIÓN DE VEHÍCULOS");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        lblTitulo.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        // FORMULARIO.
        JPanel panelFormulario = new JPanel(
                new GridLayout(8, 2, 10, 10)
        );

        panelFormulario.setBackground(
                ConfiguracionApp.COLOR_BLANCO
        );

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Datos del vehículo"
                )
        );

        txtPlaca = new JTextField(15);
        txtMarca = new JTextField(15);
        txtModelo = new JTextField(15);
        txtKilometraje = new JTextField(15);
        txtPrecio = new JTextField(15);
        txtProximoMantenimiento = new JTextField(15);

        cmbTipo = new JComboBox<>(TipoVehiculo.values());
        cmbEstado = new JComboBox<>(EstadoVehiculo.values());

        panelFormulario.add(new JLabel("Placa:"));
        panelFormulario.add(txtPlaca);

        panelFormulario.add(new JLabel("Marca:"));
        panelFormulario.add(txtMarca);

        panelFormulario.add(new JLabel("Modelo:"));
        panelFormulario.add(txtModelo);

        panelFormulario.add(new JLabel("Tipo de vehículo:"));
        panelFormulario.add(cmbTipo);

        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(cmbEstado);

        panelFormulario.add(new JLabel("Kilometraje actual:"));
        panelFormulario.add(txtKilometraje);

        panelFormulario.add(new JLabel("Precio por día (S/):"));
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Próximo mantenimiento (km):"));
        panelFormulario.add(txtProximoMantenimiento);

        // BOTONES.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        panelBotones.setBackground(
                ConfiguracionApp.COLOR_FONDO
        );

        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnAgregar.setBackground(
                ConfiguracionApp.COLOR_PRIMARIO
        );
        btnAgregar.setForeground(Color.WHITE);

        btnEliminar.setBackground(
                ConfiguracionApp.COLOR_ERROR
        );
        btnEliminar.setForeground(Color.WHITE);

        btnLimpiar.setBackground(
                ConfiguracionApp.COLOR_SECUNDARIO
        );
        btnLimpiar.setForeground(Color.WHITE);

        btnAgregar.setFocusPainted(false);
        btnEliminar.setFocusPainted(false);
        btnLimpiar.setFocusPainted(false);

        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAgregar.addActionListener(e -> agregarVehiculo());
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // PANEL SUPERIOR.
        JPanel panelSuperior = new JPanel(
                new BorderLayout(5, 5)
        );

        panelSuperior.setBackground(
                ConfiguracionApp.COLOR_FONDO
        );

        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        // TABLA.
        String[] columnas = {
            "Placa",
            "Marca",
            "Modelo",
            "Tipo",
            "Estado",
            "Kilometraje",
            "Precio por día",
            "Próximo mantenimiento"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloTabla);

        tablaVehiculos.setRowHeight(28);
        tablaVehiculos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaVehiculos.getTableHeader().setFont(
                ConfiguracionApp.FUENTE_BOTON
        );

        tablaVehiculos.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tablaVehiculos);

        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Listado de vehículos"
                )
        );

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // CARGAR VEHÍCULOS DESDE SQLITE.
    private void cargarVehiculos() {

        try {
            modeloTabla.setRowCount(0);

            List<Vehiculo> vehiculos = vehiculoDAO.listarTodos();

            for (Vehiculo vehiculo : vehiculos) {
                agregarFila(vehiculo);
            }

        } catch (RuntimeException ex) {
            mostrarError(
                    "No se pudieron cargar los vehículos:\n"
                    + ex.getMessage()
            );
        }
    }

    // AGREGAR UNA FILA A LA TABLA.
    private void agregarFila(Vehiculo vehiculo) {

        modeloTabla.addRow(new Object[]{
            vehiculo.getPlaca(),
            vehiculo.getMarca(),
            vehiculo.getModelo(),
            vehiculo.getTipo().getEtiqueta(),
            vehiculo.getEstado().getEtiqueta(),
            vehiculo.getKilometraje(),
            vehiculo.getPrecioBasePorDia(),
            vehiculo.getKilometrajeProximoMantenimiento()
        });
    }

    // REGISTRAR VEHÍCULO EN SQLITE.
    private void agregarVehiculo() {

        String placa = txtPlaca.getText()
                .trim().toUpperCase();

        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String kilometraje = txtKilometraje.getText().trim();
        String precio = txtPrecio.getText().trim();
        String proximoMantenimiento =
                txtProximoMantenimiento.getText().trim();

        // Validar placa.
        if (!Validador.esPlacaValida(placa)) {
            mostrarMensaje(
                    "Ingrese una placa válida. Ejemplo: ABC-123."
            );
            txtPlaca.requestFocus();
            return;
        }

        // Validar marca y modelo.
        if (!Validador.esTextoValido(marca)
                || !Validador.esTextoValido(modelo)) {

            mostrarMensaje(
                    "Complete correctamente la marca y el modelo."
            );
            return;
        }

        // Validar kilometraje actual.
        if (!Validador.esNumeroNoNegativo(kilometraje)) {
            mostrarMensaje("Ingrese un kilometraje válido.");
            txtKilometraje.requestFocus();
            return;
        }

        // Validar precio.
        if (!Validador.esNumeroPositivo(precio)) {
            mostrarMensaje(
                    "Ingrese un precio por día mayor que cero."
            );
            txtPrecio.requestFocus();
            return;
        }

        // Validar próximo mantenimiento.
        if (!Validador.esNumeroPositivo(proximoMantenimiento)) {
            mostrarMensaje(
                    "Ingrese un kilometraje de próximo mantenimiento válido."
            );
            txtProximoMantenimiento.requestFocus();
            return;
        }

        try {
            // Comprobar que la placa no exista en SQLite.
            if (vehiculoDAO.buscarPorId(placa).isPresent()) {
                mostrarMensaje(
                        "Ya existe un vehículo registrado con esa placa."
                );
                txtPlaca.requestFocus();
                return;
            }

            double km = Double.parseDouble(kilometraje);
            double precioDia = Double.parseDouble(precio);
            double kmProximo =
                    Double.parseDouble(proximoMantenimiento);

            if (kmProximo <= km) {
                mostrarMensaje(
                        "El próximo mantenimiento debe ser mayor "
                        + "que el kilometraje actual."
                );
                txtProximoMantenimiento.requestFocus();
                return;
            }

            // Construir el objeto del dominio.
            Vehiculo vehiculo = new Vehiculo(
                    placa,
                    marca,
                    modelo,
                    (TipoVehiculo) cmbTipo.getSelectedItem(),
                    (EstadoVehiculo) cmbEstado.getSelectedItem(),
                    km,
                    precioDia,
                    kmProximo
            );

            // Guardar permanentemente en SQLite.
            vehiculoDAO.guardar(vehiculo);

            // Actualizar la tabla.
            agregarFila(vehiculo);

            mostrarMensaje(
                    "Vehículo registrado correctamente en la base de datos."
            );

            limpiarCampos();

        } catch (NumberFormatException ex) {
            mostrarMensaje(
                    "Verifique que los valores numéricos sean correctos."
            );

        } catch (RuntimeException ex) {
            mostrarError(
                    "No se pudo registrar el vehículo:\n"
                    + ex.getMessage()
            );
        }
    }

    // ELIMINAR VEHÍCULO DE SQLITE.
    private void eliminarVehiculo() {

        int filaSeleccionada = tablaVehiculos.getSelectedRow();

        if (filaSeleccionada == -1) {
            mostrarMensaje(
                    "Seleccione un vehículo de la tabla."
            );
            return;
        }

        int fila = tablaVehiculos.convertRowIndexToModel(
                filaSeleccionada
        );

        String placa = modeloTabla.getValueAt(fila, 0).toString();

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el vehículo con placa "
                + placa + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {
                // Eliminar permanentemente de SQLite.
                vehiculoDAO.eliminar(placa);

                // Quitar de la tabla después de eliminar correctamente.
                modeloTabla.removeRow(fila);

                mostrarMensaje(
                        "Vehículo eliminado correctamente."
                );

            } catch (RuntimeException ex) {
                mostrarError(
                        "No se pudo eliminar el vehículo:\n"
                        + ex.getMessage()
                );
            }
        }
    }

    // LIMPIAR CAMPOS.
    public void limpiarCampos() {

        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtKilometraje.setText("");
        txtPrecio.setText("");
        txtProximoMantenimiento.setText("");

        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);

        txtPlaca.requestFocusInWindow();
    }

    // MENSAJES.
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
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ACCESO A LA TABLA.
    public JTable getTablaVehiculos() {
        return tablaVehiculos;
    }
}
