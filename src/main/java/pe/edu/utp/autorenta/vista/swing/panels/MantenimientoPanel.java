/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.util.Validador;
import pe.edu.utp.autorenta.util.FechaUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class MantenimientoPanel extends JPanel {

    private JTextField txtPlaca;
    private JTextField txtDescripcion;
    private JTextField txtFecha;
    private JTextField txtCosto;

    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbEstado;

    private JTable tablaMantenimientos;
    private DefaultTableModel modeloTabla;

    private JButton btnRegistrar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public MantenimientoPanel() {
        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel("GESTIÓN DE MANTENIMIENTOS");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        add(lblTitulo, BorderLayout.NORTH);

        // Formulario.
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_BLANCO);
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del mantenimiento"));

        txtPlaca = new JTextField();
        txtDescripcion = new JTextField();
        txtFecha = new JTextField();
        txtCosto = new JTextField();

        cmbTipo = new JComboBox<>(new String[]{
            "PREVENTIVO",
            "CORRECTIVO"
        });

        cmbEstado = new JComboBox<>(new String[]{
            "PENDIENTE",
            "EN_PROCESO",
            "COMPLETADO"
        });

        panelFormulario.add(new JLabel("Placa del vehículo:"));
        panelFormulario.add(txtPlaca);

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

        btnRegistrar.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        btnRegistrar.setForeground(Color.WHITE);

        btnEliminar.setBackground(ConfiguracionApp.COLOR_ERROR);
        btnEliminar.setForeground(Color.WHITE);

        btnLimpiar.setBackground(ConfiguracionApp.COLOR_SECUNDARIO);
        btnLimpiar.setForeground(Color.WHITE);

        btnRegistrar.addActionListener(e -> registrarMantenimiento());
        btnEliminar.addActionListener(e -> eliminarMantenimiento());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.setBackground(ConfiguracionApp.COLOR_FONDO);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.CENTER);

        // Tabla.
        String[] columnas = {
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
        tablaMantenimientos.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tablaMantenimientos);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Listado de mantenimientos"));

        add(scroll, BorderLayout.SOUTH);
    }

    // Registra un mantenimiento en la tabla.
    private void registrarMantenimiento() {

        String placa = txtPlaca.getText().trim().toUpperCase();
        String descripcion = txtDescripcion.getText().trim();
        String fecha = txtFecha.getText().trim();
        String costo = txtCosto.getText().trim();

        if (!Validador.esPlacaValida(placa)) {
            mostrarMensaje("Ingrese una placa válida. Ejemplo: ABC-123.");
            txtPlaca.requestFocus();
            return;
        }

        if (!Validador.esTextoValido(descripcion)) {
            mostrarMensaje("Ingrese una descripción.");
            txtDescripcion.requestFocus();
            return;
        }

        LocalDate fechaConvertida = FechaUtil.convertirAFecha(fecha);

        if (fechaConvertida == null) {
            mostrarMensaje("Ingrese una fecha válida en formato dd/MM/yyyy.");
            txtFecha.requestFocus();
            return;
        }

        if (!Validador.esNumeroNoNegativo(costo)) {
            mostrarMensaje("Ingrese un costo válido.");
            txtCosto.requestFocus();
            return;
        }

        modeloTabla.addRow(new Object[]{
            placa,
            descripcion,
            fecha,
            costo,
            cmbTipo.getSelectedItem(),
            cmbEstado.getSelectedItem()
        });

        mostrarMensaje("Mantenimiento agregado a la tabla.");
        limpiarCampos();
    }

    // Elimina el mantenimiento seleccionado.
    private void eliminarMantenimiento() {

        int fila = tablaMantenimientos.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccione un mantenimiento de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este mantenimiento?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(fila);
            mostrarMensaje("Mantenimiento eliminado de la tabla.");
        }
    }

    // Limpia los campos del formulario.
    public void limpiarCampos() {
        txtPlaca.setText("");
        txtDescripcion.setText("");
        txtFecha.setText("");
        txtCosto.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        txtPlaca.requestFocus();
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

    public JTable getTablaMantenimientos() {
        return tablaMantenimientos;
    }
}

