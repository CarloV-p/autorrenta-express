/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.domain.enums.TipoVehiculo;
import pe.edu.utp.autorenta.domain.enums.EstadoVehiculo;
import pe.edu.utp.autorenta.util.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VehiculoPanel extends JPanel {

    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtKilometraje;
    private JTextField txtPrecio;

    private JComboBox<TipoVehiculo> cmbTipo;
    private JComboBox<EstadoVehiculo> cmbEstado;

    private JTable tablaVehiculos;
    private DefaultTableModel modeloTabla;

    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public VehiculoPanel() {
        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {

        // Título.
        JLabel lblTitulo = new JLabel("GESTIÓN DE VEHÍCULOS");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        add(lblTitulo, BorderLayout.NORTH);

        // Formulario.
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 10, 10));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_BLANCO);
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del vehículo"));

        txtPlaca = new JTextField();
        txtMarca = new JTextField();
        txtModelo = new JTextField();
        txtKilometraje = new JTextField();
        txtPrecio = new JTextField();

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

        panelFormulario.add(new JLabel("Kilometraje:"));
        panelFormulario.add(txtKilometraje);

        panelFormulario.add(new JLabel("Precio por día (S/):"));
        panelFormulario.add(txtPrecio);

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        btnAgregar.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        btnAgregar.setForeground(Color.WHITE);

        btnEliminar.setBackground(ConfiguracionApp.COLOR_ERROR);
        btnEliminar.setForeground(Color.WHITE);

        btnLimpiar.setBackground(ConfiguracionApp.COLOR_SECUNDARIO);
        btnLimpiar.setForeground(Color.WHITE);

        btnAgregar.addActionListener(e -> agregarVehiculo());
        btnEliminar.addActionListener(e -> eliminarVehiculo());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.setBackground(ConfiguracionApp.COLOR_FONDO);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.CENTER);

        // Tabla de vehículos.
        String[] columnas = {
            "Placa",
            "Marca",
            "Modelo",
            "Tipo",
            "Estado",
            "Kilometraje",
            "Precio por día"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloTabla);
        tablaVehiculos.setRowHeight(25);
        tablaVehiculos.getTableHeader().setFont(
                ConfiguracionApp.FUENTE_BOTON);

        JScrollPane scroll = new JScrollPane(tablaVehiculos);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Listado de vehículos"));

        // La tabla se coloca en la parte inferior.
        add(scroll, BorderLayout.SOUTH);
    }

    // Agrega un vehículo a la tabla de la interfaz.
    private void agregarVehiculo() {

        String placa = txtPlaca.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String kilometraje = txtKilometraje.getText().trim();
        String precio = txtPrecio.getText().trim();

        if (!Validador.esPlacaValida(placa)) {
            mostrarMensaje("Ingrese una placa válida. Ejemplo: ABC-123.");
            txtPlaca.requestFocus();
            return;
        }

        if (!Validador.esTextoValido(marca)
                || !Validador.esTextoValido(modelo)) {
            mostrarMensaje("Complete la marca y el modelo.");
            return;
        }

        if (!Validador.esNumeroNoNegativo(kilometraje)) {
            mostrarMensaje("Ingrese un kilometraje válido.");
            return;
        }

        if (!Validador.esNumeroPositivo(precio)) {
            mostrarMensaje("Ingrese un precio por día mayor que cero.");
            return;
        }

        // Evita placas repetidas en la tabla.
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).toString()
                    .equalsIgnoreCase(placa)) {
                mostrarMensaje("Ya existe un vehículo con esa placa.");
                return;
            }
        }

        modeloTabla.addRow(new Object[]{
            placa,
            marca,
            modelo,
            cmbTipo.getSelectedItem(),
            cmbEstado.getSelectedItem(),
            kilometraje,
            precio
        });

        mostrarMensaje("Vehículo agregado a la tabla.");
        limpiarCampos();
    }

    // Elimina el vehículo seleccionado de la tabla.
    private void eliminarVehiculo() {

        int fila = tablaVehiculos.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccione un vehículo de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el vehículo seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(fila);
            mostrarMensaje("Vehículo eliminado de la tabla.");
        }
    }

    // Limpia los campos del formulario.
    public void limpiarCampos() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtKilometraje.setText("");
        txtPrecio.setText("");

        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);

        txtPlaca.requestFocus();
    }

    // Muestra mensajes del sistema.
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "AutoRenta Express",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Permite consultar la tabla desde otras clases.
    public JTable getTablaVehiculos() {
        return tablaVehiculos;
    }
}
