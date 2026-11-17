/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientePanel extends JPanel {

    private JTextField txtNombre;
    private JTextField txtDni;
    private JTextField txtTelefono;
    private JTextField txtCorreo;

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;

    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public ClientePanel() {
        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel("GESTIÓN DE CLIENTES");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        add(lblTitulo, BorderLayout.NORTH);

        // Formulario de clientes.
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_BLANCO);
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del cliente"));

        txtNombre = new JTextField();
        txtDni = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        panelFormulario.add(new JLabel("Nombre completo:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("DNI:"));
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);

        panelFormulario.add(new JLabel("Correo electrónico:"));
        panelFormulario.add(txtCorreo);

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10));
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

        btnAgregar.addActionListener(e -> agregarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.setBackground(ConfiguracionApp.COLOR_FONDO);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.CENTER);

        // Tabla de clientes.
        String[] columnas = {
            "Nombre",
            "DNI",
            "Teléfono",
            "Correo electrónico"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Listado de clientes"));

        add(scroll, BorderLayout.SOUTH);
    }

    // Agrega un cliente a la tabla.
    private void agregarCliente() {

        String nombre = txtNombre.getText().trim();
        String dni = txtDni.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

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

        if (!Validador.esCorreoValido(correo)) {
            mostrarMensaje("Ingrese un correo electrónico válido.");
            txtCorreo.requestFocus();
            return;
        }

        // Evita registrar DNI repetidos.
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 1).toString().equals(dni)) {
                mostrarMensaje("Ya existe un cliente con ese DNI.");
                return;
            }
        }

        modeloTabla.addRow(new Object[]{
            nombre,
            dni,
            telefono,
            correo
        });

        mostrarMensaje("Cliente agregado a la tabla.");
        limpiarCampos();
    }

    // Elimina el cliente seleccionado.
    private void eliminarCliente() {

        int fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccione un cliente de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este cliente?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(fila);
            mostrarMensaje("Cliente eliminado de la tabla.");
        }
    }

    // Limpia el formulario.
    public void limpiarCampos() {
        txtNombre.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtNombre.requestFocus();
    }

    // Muestra mensajes.
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "AutoRenta Express",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public JTable getTablaClientes() {
        return tablaClientes;
    }
}
