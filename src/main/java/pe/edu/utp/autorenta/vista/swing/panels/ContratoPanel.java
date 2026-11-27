
package pe.edu.utp.autorenta.vista.swing.panels;

// @author Marlee Lucero Romero Pequeña

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
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

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.config.ConexionBD;

import pe.edu.utp.autorenta.domain.enums.TipoContrato;
import pe.edu.utp.autorenta.domain.enums.TipoCobro;
import pe.edu.utp.autorenta.domain.model.Cliente;
import pe.edu.utp.autorenta.domain.model.Vehiculo;
import pe.edu.utp.autorenta.domain.model.ContratoAlquiler;
import pe.edu.utp.autorenta.domain.model.ContratoCorporativo;

import pe.edu.utp.autorenta.persistence.dao.ClienteDAO;
import pe.edu.utp.autorenta.persistence.dao.VehiculoDAO;
import pe.edu.utp.autorenta.persistence.dao.ConductorDAO;
import pe.edu.utp.autorenta.persistence.dao.CoberturaDAO;
import pe.edu.utp.autorenta.persistence.dao.ContratoDAO;

import pe.edu.utp.autorenta.persistence.dao.impl.ClienteDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.VehiculoDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.ConductorDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.CoberturaDAOImpl;
import pe.edu.utp.autorenta.persistence.dao.impl.ContratoDAOImpl;

import pe.edu.utp.autorenta.util.Validador;

public class ContratoPanel extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ConexionBD conexion;

    private ClienteDAO clienteDAO;
    private VehiculoDAO vehiculoDAO;
    private ConductorDAO conductorDAO;
    private CoberturaDAO coberturaDAO;
    private ContratoDAO contratoDAO;

    private JComboBox<Cliente> cmbCliente;
    private JComboBox<Vehiculo> cmbVehiculo;
    private JComboBox<TipoContrato> cmbTipoContrato;
    private JComboBox<TipoCobro> cmbTipoCobro;

    private JTextField txtRazonSocial;
    private JTextField txtRuc;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTextField txtImporte;

    private JTable tablaContratos;
    private DefaultTableModel modeloTabla;

    private JButton btnRegistrar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnActualizar;

    public ContratoPanel() {
        configurarDAO();
        inicializarComponentes();
        configurarEventos();
        cargarClientes();
        cargarVehiculos();
        cargarContratos();
    }

    // =========================================================
    // CONEXION CON LOS DAO
    // =========================================================

    private void configurarDAO() {

        try {
            conexion = new ConexionBD();
            conexion.inicializarEsquema();

            clienteDAO = new ClienteDAOImpl(conexion);
            vehiculoDAO = new VehiculoDAOImpl(conexion);
            conductorDAO = new ConductorDAOImpl(conexion);
            coberturaDAO = new CoberturaDAOImpl(conexion);

            contratoDAO = new ContratoDAOImpl(
                    conexion,
                    clienteDAO,
                    vehiculoDAO,
                    conductorDAO,
                    coberturaDAO
            );

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo conectar con la base de datos:\n"
                    + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // INTERFAZ GRAFICA
    // =========================================================

    private void inicializarComponentes() {

        setLayout(new BorderLayout(10, 10));
        setBackground(ConfiguracionApp.COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titulo = new JLabel("GESTIÓN DE CONTRATOS");
        titulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        titulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(ConfiguracionApp.COLOR_FONDO);
        panelSuperior.add(titulo, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        // FORMULARIO

        JPanel panelFormulario = new JPanel(new GridLayout(0, 4, 10, 8));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_BLANCO);

        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ConfiguracionApp.COLOR_PRIMARIO),
                "Datos del contrato"
        ));

        cmbTipoContrato = new JComboBox<>(TipoContrato.values());
        cmbTipoCobro = new JComboBox<>(TipoCobro.values());

        cmbCliente = new JComboBox<>();
        cmbVehiculo = new JComboBox<>();

        txtRazonSocial = new JTextField();
        txtRuc = new JTextField();
        txtFechaInicio = new JTextField();
        txtFechaFin = new JTextField();
        txtImporte = new JTextField();

        agregarCampo(panelFormulario, "Tipo de contrato:", cmbTipoContrato);
        agregarCampo(panelFormulario, "Tipo de cobro:", cmbTipoCobro);

        agregarCampo(panelFormulario, "Cliente registrado:", cmbCliente);
        agregarCampo(panelFormulario, "Vehículo:", cmbVehiculo);

        agregarCampo(panelFormulario, "Razón social:", txtRazonSocial);
        agregarCampo(panelFormulario, "RUC:", txtRuc);

        agregarCampo(panelFormulario, "Fecha inicio (dd/MM/yyyy):", txtFechaInicio);
        agregarCampo(panelFormulario, "Fecha fin (dd/MM/yyyy):", txtFechaFin);

        agregarCampo(panelFormulario, "Importe total (S/):", txtImporte);

        configurarRenderizadores();

        // BOTONES

        btnRegistrar = new JButton("Registrar contrato");
        btnEliminar = new JButton("Eliminar contrato");
        btnLimpiar = new JButton("Limpiar");
        btnActualizar = new JButton("Actualizar lista");

        estilizarBoton(btnRegistrar, ConfiguracionApp.COLOR_EXITO);
        estilizarBoton(btnEliminar, ConfiguracionApp.COLOR_ERROR);
        estilizarBoton(btnLimpiar, ConfiguracionApp.COLOR_SECUNDARIO);
        estilizarBoton(btnActualizar, ConfiguracionApp.COLOR_PRIMARIO);

        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 8));
        panelBotones.setBackground(ConfiguracionApp.COLOR_FONDO);

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnActualizar);

        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.setBackground(ConfiguracionApp.COLOR_FONDO);

        panelCentro.add(panelFormulario, BorderLayout.CENTER);
        panelCentro.add(panelBotones, BorderLayout.SOUTH);

        // TABLA

        modeloTabla = new DefaultTableModel(
                new Object[]{
                    "ID",
                    "Tipo",
                    "Cliente",
                    "DNI",
                    "Empresa",
                    "RUC",
                    "Vehículo",
                    "Fecha inicio",
                    "Fecha fin",
                    "Cobro",
                    "Importe",
                    "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaContratos = new JTable(modeloTabla);
        tablaContratos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaContratos.setRowHeight(25);

        tablaContratos.getTableHeader().setReorderingAllowed(false);

        // Ocultar ID, pero conservarlo en el modelo.
        tablaContratos.removeColumn(
                tablaContratos.getColumnModel().getColumn(0)
        );

        JScrollPane scrollTabla = new JScrollPane(tablaContratos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                "Contratos registrados"
        ));

        JPanel panelContenido = new JPanel(new BorderLayout(10, 10));
        panelContenido.setBackground(ConfiguracionApp.COLOR_FONDO);

        panelContenido.add(panelCentro, BorderLayout.NORTH);
        panelContenido.add(scrollTabla, BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, String etiqueta, java.awt.Component campo) {

        JLabel label = new JLabel(etiqueta);
        label.setFont(ConfiguracionApp.FUENTE_NORMAL);

        panel.add(label);
        panel.add(campo);
    }

    private void estilizarBoton(JButton boton, java.awt.Color color) {

        boton.setBackground(color);
        boton.setForeground(ConfiguracionApp.COLOR_BLANCO);
        boton.setFont(ConfiguracionApp.FUENTE_BOTON);
        boton.setFocusPainted(false);
    }

    private void configurarRenderizadores() {

        cmbCliente.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean tieneFoco) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco
                );

                if (valor instanceof Cliente) {
                    Cliente cliente = (Cliente) valor;
                    setText(cliente.getNombre() + " - DNI: " + cliente.getDni());
                }

                return this;
            }
        });

        cmbVehiculo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> lista,
                    Object valor,
                    int indice,
                    boolean seleccionado,
                    boolean tieneFoco) {

                super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco
                );

                if (valor instanceof Vehiculo) {
                    Vehiculo vehiculo = (Vehiculo) valor;
                    setText(
                            vehiculo.getPlaca() + " - "
                            + vehiculo.getMarca() + " "
                            + vehiculo.getModelo()
                    );
                }

                return this;
            }
        });
    }

    // =========================================================
    // EVENTOS
    // =========================================================

    private void configurarEventos() {

        btnRegistrar.addActionListener(e -> registrarContrato());

        btnEliminar.addActionListener(e -> eliminarContrato());

        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnActualizar.addActionListener(e -> actualizarDatos());

        cmbTipoContrato.addActionListener(e -> actualizarCamposCorporativos());
    }

    private void actualizarCamposCorporativos() {

        boolean corporativo =
                cmbTipoContrato.getSelectedItem() == TipoContrato.CORPORATIVO;

        txtRazonSocial.setEnabled(corporativo);
        txtRuc.setEnabled(corporativo);

        if (!corporativo) {
            txtRazonSocial.setText("");
            txtRuc.setText("");
        }
    }

    // =========================================================
    // CARGAR CLIENTES
    // =========================================================

    private void cargarClientes() {

        if (clienteDAO == null) {
            return;
        }

        try {
            cmbCliente.removeAllItems();

            List<Cliente> clientes = clienteDAO.listarTodos();

            for (Cliente cliente : clientes) {
                cmbCliente.addItem(cliente);
            }

            if (clientes.isEmpty()) {
                mostrarMensaje(
                        "No hay clientes registrados. Registra un cliente primero.",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (RuntimeException e) {
            mostrarMensaje(
                    "Error al cargar clientes:\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CARGAR VEHICULOS
    // =========================================================

    private void cargarVehiculos() {

        if (vehiculoDAO == null) {
            return;
        }

        try {
            cmbVehiculo.removeAllItems();

            List<Vehiculo> vehiculos = vehiculoDAO.listarTodos();

            int disponibles = 0;

            for (Vehiculo vehiculo : vehiculos) {

                if (vehiculo.estaDisponible()) {
                    cmbVehiculo.addItem(vehiculo);
                    disponibles++;
                }
            }

            if (disponibles == 0) {
                mostrarMensaje(
                        "No hay vehículos disponibles para registrar contratos.",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (RuntimeException e) {
            mostrarMensaje(
                    "Error al cargar vehículos:\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // REGISTRAR CONTRATO
    // =========================================================

    private void registrarContrato() {

        if (contratoDAO == null || clienteDAO == null || vehiculoDAO == null) {
            mostrarMensaje(
                    "No se pudo inicializar la conexión con la base de datos.",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {

            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            Vehiculo vehiculo = (Vehiculo) cmbVehiculo.getSelectedItem();

            TipoContrato tipo = (TipoContrato) cmbTipoContrato.getSelectedItem();
            TipoCobro cobro = (TipoCobro) cmbTipoCobro.getSelectedItem();

            if (cliente == null) {
                mostrarMensaje("Selecciona un cliente registrado.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (vehiculo == null) {
                mostrarMensaje("Selecciona un vehículo disponible.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tipo == null || cobro == null) {
                mostrarMensaje("Selecciona el tipo de contrato y de cobro.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Validador.esNumeroPositivo(txtImporte.getText())) {
                mostrarMensaje("El importe debe ser un número mayor que cero.", JOptionPane.WARNING_MESSAGE);
                txtImporte.requestFocus();
                return;
            }

            LocalDate fechaInicio;
            LocalDate fechaFin;

            try {
                fechaInicio = LocalDate.parse(
                        txtFechaInicio.getText().trim(),
                        FORMATO_FECHA
                );

                fechaFin = LocalDate.parse(
                        txtFechaFin.getText().trim(),
                        FORMATO_FECHA
                );

            } catch (DateTimeParseException e) {
                mostrarMensaje(
                        "Las fechas deben tener el formato dd/MM/yyyy.\n"
                        + "Ejemplo: 27/11/2026",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (fechaFin.isBefore(fechaInicio)) {
                mostrarMensaje(
                        "La fecha de fin no puede ser anterior a la fecha de inicio.",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!vehiculo.estaDisponible()) {
                mostrarMensaje(
                        "El vehículo seleccionado no está disponible.",
                        JOptionPane.WARNING_MESSAGE
                );
                cargarVehiculos();
                return;
            }

            // Comprobar si existe otro contrato activo en esas fechas.
            if (contratoDAO.existeContratoActivoEnRango(
                    vehiculo.getPlaca(),
                    fechaInicio,
                    fechaFin
            )) {
                mostrarMensaje(
                        "El vehículo ya tiene un contrato activo "
                        + "que se cruza con las fechas seleccionadas.",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            double importe = Double.parseDouble(txtImporte.getText().trim());

            ContratoAlquiler contrato;

            if (tipo == TipoContrato.CORPORATIVO) {

                String razonSocial = txtRazonSocial.getText().trim();
                String ruc = txtRuc.getText().trim();

                if (!Validador.esTextoValido(razonSocial)) {
                    mostrarMensaje(
                            "Ingresa la razón social de la empresa.",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if (!Validador.esRucValido(ruc)) {
                    mostrarMensaje(
                            "El RUC debe tener 11 dígitos y comenzar con 10 o 20.",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                ContratoCorporativo corporativo = new ContratoCorporativo(
                        cliente,
                        vehiculo,
                        fechaInicio,
                        fechaFin,
                        razonSocial
                );

                corporativo.setRuc(ruc);
                contrato = corporativo;

            } else {

                contrato = new ContratoAlquiler(
                        cliente,
                        vehiculo,
                        fechaInicio,
                        fechaFin
                );
            }

            contrato.setTipoCobro(cobro);
            contrato.setMontoTotal(importe);

            // El depósito y kilometraje pactado quedan en cero
            // hasta que el usuario los gestione desde el módulo correspondiente.

            contratoDAO.guardar(contrato);

            mostrarMensaje(
                    "Contrato registrado correctamente.\n"
                    + "Código: " + contrato.getIdContrato(),
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            cargarContratos();
            cargarVehiculos();

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "No se pudo registrar el contrato:\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CARGAR CONTRATOS DESDE SQLITE
    // =========================================================

    private void cargarContratos() {

        if (contratoDAO == null) {
            return;
        }

        try {

            modeloTabla.setRowCount(0);

            List<ContratoAlquiler> contratos = contratoDAO.listarTodos();

            for (ContratoAlquiler contrato : contratos) {

                String razonSocial = "";
                String ruc = "";

                if (contrato instanceof ContratoCorporativo) {

                    ContratoCorporativo corporativo =
                            (ContratoCorporativo) contrato;

                    razonSocial = corporativo.getRazonSocialEmpresa();
                    ruc = corporativo.getRuc();
                }

                modeloTabla.addRow(new Object[]{
                    contrato.getIdContrato(),
                    contrato.getTipoContrato().getEtiqueta(),
                    contrato.getCliente().getNombre(),
                    contrato.getCliente().getDni(),
                    razonSocial,
                    ruc,
                    contrato.getVehiculo().getPlaca(),
                    contrato.getFechaInicio().format(FORMATO_FECHA),
                    contrato.getFechaFin().format(FORMATO_FECHA),
                    contrato.getTipoCobro().getEtiqueta(),
                    String.format("S/ %.2f", contrato.getMontoTotal()),
                    contrato.getEstadoContrato().getEtiqueta()
                });
            }

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "Error al consultar contratos:\n" + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ELIMINAR CONTRATO
    // =========================================================

    private void eliminarContrato() {

        int fila = tablaContratos.getSelectedRow();

        if (fila == -1) {
            mostrarMensaje(
                    "Selecciona un contrato de la tabla.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int filaModelo = tablaContratos.convertRowIndexToModel(fila);

        int idContrato = Integer.parseInt(
                modeloTabla.getValueAt(filaModelo, 0).toString()
        );

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar el contrato seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            contratoDAO.eliminar(idContrato);

            mostrarMensaje(
                    "Contrato eliminado correctamente.",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cargarContratos();
            cargarVehiculos();

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "No se pudo eliminar el contrato.\n"
                    + "Puede tener pagos u otros registros asociados.\n\n"
                    + e.getMessage(),
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ACTUALIZAR DATOS
    // =========================================================

    private void actualizarDatos() {
        cargarClientes();
        cargarVehiculos();
        cargarContratos();

        mostrarMensaje(
                "Información actualizada.",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    private void limpiarFormulario() {

        if (cmbTipoContrato != null) {
            cmbTipoContrato.setSelectedItem(TipoContrato.SIMPLE);
        }

        if (cmbTipoCobro != null) {
            cmbTipoCobro.setSelectedItem(TipoCobro.POR_DIA);
        }

        if (cmbCliente != null && cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }

        if (cmbVehiculo != null && cmbVehiculo.getItemCount() > 0) {
            cmbVehiculo.setSelectedIndex(0);
        }

        txtRazonSocial.setText("");
        txtRuc.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtImporte.setText("");

        actualizarCamposCorporativos();
    }

    // =========================================================
    // MENSAJES
    // =========================================================

    private void mostrarMensaje(String mensaje, int tipo) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                ConfiguracionApp.NOMBRE_APLICACION,
                tipo
        );
    }
}
