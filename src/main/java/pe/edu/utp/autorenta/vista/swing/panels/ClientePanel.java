
/*
 * Gestión de clientes de AutoRenta Express.
 */

package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.model.Cliente;
import pe.edu.utp.autorenta.persistence.dao.ClienteDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.ClienteDAOImpl;
import pe.edu.utp.autorenta.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientePanel extends JPanel {

    private JTextField txtNombre;
    private JTextField txtDni;
    private JTextField txtTelefono;

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private final ClienteDAO clienteDAO;

    public ClientePanel() {
        clienteDAO = new ClienteDAOImpl(new ConexionBD());

        configurarPanel();
        inicializarComponentes();
        cargarClientes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);

        setBorder(BorderFactory.createEmptyBorder(
                15, 15, 15, 15
        ));
    }

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel("GESTIÓN DE CLIENTES");

        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        lblTitulo.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        // FORMULARIO
        JPanel panelFormulario = new JPanel(
                new GridLayout(3, 2, 10, 12)
        );

        panelFormulario.setBackground(
                ConfiguracionApp.COLOR_BLANCO
        );

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del cliente")
        );

        txtNombre = new JTextField(20);
        txtDni = new JTextField(20);
        txtTelefono = new JTextField(20);

        panelFormulario.add(new JLabel("Nombre completo:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("DNI:"));
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);

        // BOTONES
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        panelBotones.setBackground(ConfiguracionApp.COLOR_FONDO);

        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnAgregar.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        btnAgregar.setForeground(Color.WHITE);

        btnEliminar.setBackground(ConfiguracionApp.COLOR_ERROR);
        btnEliminar.setForeground(Color.WHITE);

        btnLimpiar.setBackground(ConfiguracionApp.COLOR_SECUNDARIO);
        btnLimpiar.setForeground(Color.WHITE);

        btnAgregar.setFocusPainted(false);
        btnEliminar.setFocusPainted(false);
        btnLimpiar.setFocusPainted(false);

        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAgregar.addActionListener(e -> agregarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.setBackground(ConfiguracionApp.COLOR_FONDO);

        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        // TABLA
        String[] columnas = {
            "ID", "Nombre", "DNI", "Teléfono"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(28);

        tablaClientes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaClientes.getTableHeader().setFont(
                ConfiguracionApp.FUENTE_BOTON
        );

        tablaClientes.getTableHeader().setReorderingAllowed(false);

        // Ocultar el ID sin eliminarlo del modelo.
        tablaClientes.removeColumn(
                tablaClientes.getColumnModel().getColumn(0)
        );

        JScrollPane scroll = new JScrollPane(tablaClientes);

        scroll.setBorder(
                BorderFactory.createTitledBorder("Listado de clientes")
        );

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // AGREGAR CLIENTE A LA BASE DE DATOS
    private void agregarCliente() {

        String nombre = txtNombre.getText().trim();
        String dni = txtDni.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (!Validador.esTextoValido(nombre)) {
            mostrarMensaje("Ingrese el nombre del cliente.");
            txtNombre.requestFocus();
            return;
        }

        if (!Validador.esDniValido(dni)) {
            mostrarMensaje("El DNI debe tener exactamente 8 dígitos.");
            txtDni.requestFocus();
            return;
        }

        if (!Validador.esTelefonoValido(telefono)) {
            mostrarMensaje("Ingrese un teléfono válido de 9 a 15 dígitos.");
            txtTelefono.requestFocus();
            return;
        }

        try {
            if (clienteDAO.buscarPorDni(dni).isPresent()) {
                mostrarMensaje("Ya existe un cliente con ese DNI.");
                txtDni.requestFocus();
                return;
            }

            Cliente cliente = new Cliente(nombre, dni, telefono);

            clienteDAO.guardar(cliente);

            cargarClientes();

            mostrarMensaje("Cliente registrado correctamente.");

            limpiarCampos();

        } catch (RuntimeException ex) {
            mostrarError("No se pudo registrar el cliente.", ex);
        }
    }

    // CARGAR CLIENTES DESDE SQLITE
    private void cargarClientes() {

        try {
            modeloTabla.setRowCount(0);

            for (Cliente cliente : clienteDAO.listarTodos()) {

                modeloTabla.addRow(new Object[]{
                    cliente.getIdCliente(),
                    cliente.getNombre(),
                    cliente.getDni(),
                    cliente.getTelefono()
                });
            }

        } catch (RuntimeException ex) {
            mostrarError("No se pudieron cargar los clientes.", ex);
        }
    }

    // ELIMINAR CLIENTE DE LA BASE DE DATOS
    private void eliminarCliente() {

        int filaSeleccionada = tablaClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            mostrarMensaje("Seleccione un cliente de la tabla.");
            return;
        }

        int filaModelo = tablaClientes.convertRowIndexToModel(
                filaSeleccionada
        );

        int idCliente = Integer.parseInt(
                modeloTabla.getValueAt(filaModelo, 0).toString()
        );

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este cliente?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {
                clienteDAO.eliminar(idCliente);

                cargarClientes();

                mostrarMensaje("Cliente eliminado correctamente.");

            } catch (RuntimeException ex) {
                mostrarError(
                        "No se pudo eliminar el cliente. "
                        + "Puede estar relacionado con contratos.",
                        ex
                );
            }
        }
    }

    // LIMPIAR CAMPOS
    public void limpiarCampos() {

        txtNombre.setText("");
        txtDni.setText("");
        txtTelefono.setText("");

        txtNombre.requestFocusInWindow();
    }

    // MENSAJES
    private void mostrarMensaje(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "AutoRenta Express",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarError(String mensaje, RuntimeException ex) {

        JOptionPane.showMessageDialog(
                this,
                mensaje + "\nDetalle: " + ex.getMessage(),
                "Error de AutoRenta Express",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ACCESO A LA TABLA
    public JTable getTablaClientes() {
        return tablaClientes;
    }
}
