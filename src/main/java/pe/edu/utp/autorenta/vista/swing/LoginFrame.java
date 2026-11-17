/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.vista.swing;

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    // @author Marlee Lucero Romero Pequeña

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnSalir;

    public LoginFrame() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle(ConfiguracionApp.obtenerNombreAplicacion());
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(ConfiguracionApp.COLOR_FONDO);

        // Encabezado.
        JPanel panelTitulo = new JPanel(new GridLayout(2, 1));
        panelTitulo.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        panelTitulo.setBorder(
                BorderFactory.createEmptyBorder(25, 10, 25, 10));

        JLabel lblTitulo = new JLabel(
                "AUTORENTA EXPRESS", SwingConstants.CENTER);
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel(
                "Sistema de gestión de alquiler de vehículos",
                SwingConstants.CENTER);
        lblSubtitulo.setFont(ConfiguracionApp.FUENTE_NORMAL);
        lblSubtitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblSubtitulo);

        // Formulario.
        JPanel panelFormulario = new JPanel(new GridLayout(4, 1, 5, 8));
        panelFormulario.setBackground(ConfiguracionApp.COLOR_FONDO);
        panelFormulario.setBorder(
                BorderFactory.createEmptyBorder(25, 45, 20, 45));

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(ConfiguracionApp.FUENTE_NORMAL);

        txtUsuario = new JTextField();
        txtUsuario.setFont(ConfiguracionApp.FUENTE_NORMAL);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(ConfiguracionApp.FUENTE_NORMAL);

        txtContrasena = new JPasswordField();
        txtContrasena.setFont(ConfiguracionApp.FUENTE_NORMAL);

        panelFormulario.add(lblUsuario);
        panelFormulario.add(txtUsuario);
        panelFormulario.add(lblContrasena);
        panelFormulario.add(txtContrasena);

        // Botones.
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.setBackground(ConfiguracionApp.COLOR_FONDO);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(ConfiguracionApp.FUENTE_BOTON);
        btnIngresar.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);

        btnSalir = new JButton("Salir");
        btnSalir.setFont(ConfiguracionApp.FUENTE_BOTON);
        btnSalir.setBackground(ConfiguracionApp.COLOR_ERROR);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);

        panelBotones.add(btnIngresar);
        panelBotones.add(btnSalir);

        // Acciones.
        btnSalir.addActionListener(e -> System.exit(0));

        // Distribución.
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    // Obtiene el usuario ingresado.
    public String obtenerUsuario() {
        return txtUsuario.getText().trim();
    }

    // Obtiene la contraseña ingresada.
    public String obtenerContrasena() {
        return new String(txtContrasena.getPassword());
    }

    // Permite acceder al botón Ingresar desde el controlador.
    public JButton getBtnIngresar() {
        return btnIngresar;
    }

    // Limpia los campos del formulario.
    public void limpiarCampos() {
        txtUsuario.setText("");
        txtContrasena.setText("");
    }

    // Muestra mensajes al usuario.
    public void mostrarMensaje(String mensaje, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje,
                "AutoRenta Express", tipo);
    }
}
