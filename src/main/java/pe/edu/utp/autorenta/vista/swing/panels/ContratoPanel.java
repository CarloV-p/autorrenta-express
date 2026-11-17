/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.vista.swing.panels;

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.util.Validador;
import pe.edu.utp.autorenta.util.FechaUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ContratoPanel extends JPanel {

    // @author Marlee Lucero Romero Pequeña

    private JComboBox<String> cmbTipoContrato;
    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtPlaca;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtImporte;

    private JTable tablaContratos;
    private DefaultTableModel modeloTabla;

    private JButton btnRegistrar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public ContratoPanel() {
        configurarPanel();
        inicializarComponentes();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void inicializarComponentes() {

        JLabel lblTitulo = new JLabel("GESTIÓN DE CONTRATOS");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        add(lblTitulo, BorderLayout.NORTH);

        // Formulario.
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 10, 10));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_BLANCO);
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del contrato"));

        cmbTipoContrato = new JComboBox<>(new String[]{
            "PERSONA NATURAL",
            "CORPORATIVO"
        });

        txtDocumento = new JTextField();
        txtNombre = new JTextField();
        txtPlaca = new JTextField();
        txtFechaInicio = new JTextField();
        txtFechaFin = new JTextField();
        txtImporte = new JTextField();

        panelFormulario.add(new JLabel("Tipo de contrato:"));
        panelFormulario.add(cmbTipoContrato);

        panelFormulario.add(new JLabel("DNI / RUC:"));
        panelFormulario.add(txtDocumento);

        panelFormulario.add(new JLabel("Nombre / Empresa:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Placa del vehículo:"));
        panelFormulario.add(txtPlaca);

        panelFormulario.add(new JLabel("Fecha de inicio (dd/MM/yyyy):"));
        panelFormulario.add(txtFechaInicio);

        panelFormulario.add(new JLabel("Fecha de fin (dd/MM/yyyy):"));
        panelFormulario.add(txtFechaFin);

        panelFormulario.add(new JLabel("Importe (S/):"));
        panelFormulario.add(txtImporte);

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

        btnRegistrar.addActionListener(e -> registrarContrato());
        btnEliminar.addActionListener(e -> eliminarContrato());
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
            "Tipo",
            "DNI / RUC",
            "Cliente / Empresa",
            "Placa",
            "Fecha inicio",
            "Fecha fin",
            "Importe (S/)"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaContratos = new JTable(modeloTabla);
        tablaContratos.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(tablaContratos);
        scroll.setBorder(
                BorderFactory.createTitledBorder("Listado de contratos"));

        add(scroll, BorderLayout.SOUTH);
    }

    // Registra un contrato en la tabla de la interfaz.
    private void registrarContrato() {

        String tipo = cmbTipoContrato.getSelectedItem().toString();
        String documento = txtDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();
        String placa = txtPlaca.getText().trim().toUpperCase();
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        String importe = txtImporte.getText().trim();

        // Validación del DNI o RUC.
        if (tipo.equals("PERSONA NATURAL")
                && !Validador.esDniValido(documento)) {
            mostrarMensaje("El DNI debe tener exactamente 8 dígitos.");
            txtDocumento.requestFocus();
            return;
        }

        if (tipo.equals("CORPORATIVO")
                && !Validador.esRucValido(documento)) {
            mostrarMensaje("Ingrese un RUC válido de 11 dígitos.");
            txtDocumento.requestFocus();
            return;
        }

        if (!Validador.esTextoValido(nombre)) {
            mostrarMensaje("Ingrese el nombre del cliente o empresa.");
            txtNombre.requestFocus();
            return;
        }

        if (!Validador.esPlacaValida(placa)) {
            mostrarMensaje("Ingrese una placa válida. Ejemplo: ABC-123.");
            txtPlaca.requestFocus();
            return;
        }

        // Validación de fechas.
        LocalDate inicio = FechaUtil.convertirAFecha(fechaInicio);
        LocalDate fin = FechaUtil.convertirAFecha(fechaFin);

        if (inicio == null || fin == null) {
            mostrarMensaje("Ingrese fechas válidas en formato dd/MM/yyyy.");
            return;
        }

        if (!FechaUtil.esRangoValido(inicio, fin)) {
            mostrarMensaje("La fecha final no puede ser anterior a la inicial.");
            return;
        }

        if (!Validador.esNumeroPositivo(importe)) {
            mostrarMensaje("Ingrese un importe mayor que cero.");
            txtImporte.requestFocus();
            return;
        }

        // Agregar registro a la tabla.
        modeloTabla.addRow(new Object[]{
            tipo,
            documento,
            nombre,
            placa,
            fechaInicio,
            fechaFin,
            importe
        });

        mostrarMensaje("Contrato agregado a la tabla.");
        limpiarCampos();
    }

    // Elimina el contrato seleccionado.
    private void eliminarContrato() {

        int fila = tablaContratos.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje("Seleccione un contrato de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar este contrato?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(fila);
            mostrarMensaje("Contrato eliminado de la tabla.");
        }
    }

    // Limpia los campos.
    public void limpiarCampos() {
        cmbTipoContrato.setSelectedIndex(0);
        txtDocumento.setText("");
        txtNombre.setText("");
        txtPlaca.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtImporte.setText("");
        txtDocumento.requestFocus();
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

    public JTable getTablaContratos() {
        return tablaContratos;
    }
}
