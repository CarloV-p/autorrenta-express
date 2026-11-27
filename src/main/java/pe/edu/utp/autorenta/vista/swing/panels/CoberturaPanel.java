
package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import pe.edu.utp.autorenta.config.ConexionBD;
import pe.edu.utp.autorenta.domain.enums.TipoCobertura;
import pe.edu.utp.autorenta.domain.model.CoberturaSeguro;
import pe.edu.utp.autorenta.persistence.dao.CoberturaDAO;
import pe.edu.utp.autorenta.persistence.dao.impl.CoberturaDAOImpl;

public class CoberturaPanel extends JPanel {

    private CoberturaDAO coberturaDAO;

    private JComboBox<TipoCobertura> cmbTipo;
    private JTextField txtNombre;
    private JTextField txtCosto;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRecargar;

    private int idSeleccionado = -1;

    public CoberturaPanel() {
        inicializarComponentes();
        inicializarPersistencia();
        cargarCoberturas();
    }

    // Construye la interfaz del módulo.
    private void inicializarComponentes() {

        setLayout(new BorderLayout(10, 10));

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("GESTIÓN DE COBERTURAS Y EXTRAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        panelSuperior.add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 10));

        formulario.setBorder(
                BorderFactory.createTitledBorder("Datos de la cobertura")
        );

        cmbTipo = new JComboBox<>(TipoCobertura.values());
        txtNombre = new JTextField();
        txtCosto = new JTextField();

        formulario.add(new JLabel("Tipo de cobertura:"));
        formulario.add(cmbTipo);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Costo por día (S/):"));
        formulario.add(txtCosto);

        panelSuperior.add(formulario, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de coberturas.
        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Tipo",
                    "Nombre",
                    "Costo por día (S/)"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
        tabla.getTableHeader().setReorderingAllowed(false);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botones.
        JPanel panelBotones = new JPanel();

        btnRegistrar = new JButton("Registrar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnRecargar = new JButton("Recargar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRecargar);

        add(panelBotones, BorderLayout.SOUTH);

        // Estado inicial de los botones.
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        // Eventos.
        btnRegistrar.addActionListener(e -> registrarCobertura());
        btnActualizar.addActionListener(e -> actualizarCobertura());
        btnEliminar.addActionListener(e -> eliminarCobertura());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnRecargar.addActionListener(e -> cargarCoberturas());

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarCobertura();
            }
        });
    }

    // Inicializa la conexión y el DAO.
    private void inicializarPersistencia() {

        try {
            ConexionBD conexion = new ConexionBD();

            conexion.inicializarEsquema();

            coberturaDAO = new CoberturaDAOImpl(conexion);

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo conectar con la base de datos:\n"
                    + ex.getMessage()
            );
        }
    }

    // Carga las coberturas registradas en la base de datos.
    private void cargarCoberturas() {

        if (coberturaDAO == null) {
            return;
        }

        try {
            modeloTabla.setRowCount(0);

            List<CoberturaSeguro> coberturas =
                    coberturaDAO.listarTodos();

            for (CoberturaSeguro cobertura : coberturas) {

                modeloTabla.addRow(new Object[]{
                    cobertura.getIdCobertura(),
                    cobertura.getTipo().getEtiqueta(),
                    cobertura.getNombre(),
                    cobertura.getCostoPorDia()
                });
            }

        } catch (Exception ex) {
            mostrarError(
                    "No se pudieron cargar las coberturas:\n"
                    + ex.getMessage()
            );
        }
    }

    // Registra una nueva cobertura.
    private void registrarCobertura() {

        if (coberturaDAO == null) {
            mostrarError("No existe conexión con la base de datos.");
            return;
        }

        try {
            TipoCobertura tipo =
                    (TipoCobertura) cmbTipo.getSelectedItem();

            String nombre = txtNombre.getText().trim();

            String costoTexto = txtCosto.getText()
                    .trim()
                    .replace(",", ".");

            if (tipo == null || nombre.isEmpty() || costoTexto.isEmpty()) {
                mostrarError("Completa todos los campos.");
                return;
            }

            double costo = Double.parseDouble(costoTexto);

            if (!Double.isFinite(costo) || costo < 0) {
                mostrarError(
                        "El costo debe ser un número válido y no negativo."
                );
                return;
            }

            CoberturaSeguro cobertura =
                    new CoberturaSeguro(tipo, nombre, costo);

            coberturaDAO.guardar(cobertura);

            JOptionPane.showMessageDialog(
                    this,
                    "Cobertura registrada correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarCoberturas();

        } catch (NumberFormatException ex) {

            mostrarError("Ingresa un costo numérico válido.");

        } catch (Exception ex) {

            mostrarError(
                    "No se pudo registrar la cobertura.\n"
                    + "Comprueba que no exista otra cobertura del mismo tipo.\n\n"
                    + ex.getMessage()
            );
        }
    }

    // Carga los datos de la cobertura seleccionada.
    private void seleccionarCobertura() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return;
        }

        try {
            int filaModelo = tabla.convertRowIndexToModel(fila);

            idSeleccionado = Integer.parseInt(
                    modeloTabla.getValueAt(filaModelo, 0).toString()
            );

            CoberturaSeguro cobertura = coberturaDAO
                    .buscarPorId(idSeleccionado)
                    .orElse(null);

            if (cobertura == null) {
                mostrarError("No se encontró la cobertura seleccionada.");
                limpiarFormulario();
                cargarCoberturas();
                return;
            }

            cmbTipo.setSelectedItem(cobertura.getTipo());

            txtNombre.setText(cobertura.getNombre());

            txtCosto.setText(
                    String.valueOf(cobertura.getCostoPorDia())
            );

            // El tipo no se modifica porque es único en la base de datos.
            cmbTipo.setEnabled(false);

            btnRegistrar.setEnabled(false);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);

        } catch (Exception ex) {
            mostrarError(
                    "No se pudo seleccionar la cobertura:\n"
                    + ex.getMessage()
            );
        }
    }

    // Actualiza el nombre y el costo de una cobertura.
    private void actualizarCobertura() {

        if (idSeleccionado == -1 || coberturaDAO == null) {
            mostrarError("Selecciona una cobertura de la tabla.");
            return;
        }

        try {
            String nombre = txtNombre.getText().trim();

            String costoTexto = txtCosto.getText()
                    .trim()
                    .replace(",", ".");

            if (nombre.isEmpty() || costoTexto.isEmpty()) {
                mostrarError("Completa el nombre y el costo.");
                return;
            }

            double costo = Double.parseDouble(costoTexto);

            if (!Double.isFinite(costo) || costo < 0) {
                mostrarError(
                        "El costo debe ser un número válido y no negativo."
                );
                return;
            }

            CoberturaSeguro cobertura = coberturaDAO
                    .buscarPorId(idSeleccionado)
                    .orElse(null);

            if (cobertura == null) {
                mostrarError("La cobertura ya no existe.");

                limpiarFormulario();
                cargarCoberturas();
                return;
            }

            cobertura.setNombre(nombre);
            cobertura.setCostoPorDia(costo);

            coberturaDAO.actualizar(cobertura);

            JOptionPane.showMessageDialog(
                    this,
                    "Cobertura actualizada correctamente.",
                    "Actualización exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarCoberturas();

        } catch (NumberFormatException ex) {

            mostrarError("Ingresa un costo numérico válido.");

        } catch (Exception ex) {

            mostrarError(
                    "No se pudo actualizar la cobertura:\n"
                    + ex.getMessage()
            );
        }
    }

    // Elimina la cobertura seleccionada.
    private void eliminarCobertura() {

        if (idSeleccionado == -1 || coberturaDAO == null) {
            mostrarError("Selecciona una cobertura de la tabla.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar esta cobertura?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            coberturaDAO.eliminar(idSeleccionado);

            JOptionPane.showMessageDialog(
                    this,
                    "Cobertura eliminada correctamente.",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarCoberturas();

        } catch (Exception ex) {

            mostrarError(
                    "No se pudo eliminar la cobertura.\n"
                    + "Es posible que esté asociada a un contrato.\n\n"
                    + ex.getMessage()
            );
        }
    }

    // Limpia el formulario y restablece los botones.
    private void limpiarFormulario() {

        idSeleccionado = -1;

        tabla.clearSelection();

        cmbTipo.setEnabled(true);
        cmbTipo.setSelectedIndex(0);

        txtNombre.setText("");
        txtCosto.setText("");

        btnRegistrar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    // Muestra mensajes de error.
    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Módulo de Coberturas",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
