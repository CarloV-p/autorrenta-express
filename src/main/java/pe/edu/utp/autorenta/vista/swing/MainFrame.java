/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package pe.edu.utp.autorenta.vista.swing;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.vista.swing.panels.VehiculoPanel;
import pe.edu.utp.autorenta.vista.swing.panels.ClientePanel;
import pe.edu.utp.autorenta.vista.swing.panels.ContratoPanel;
import pe.edu.utp.autorenta.vista.swing.panels.MantenimientoPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel panelContenido;
    private CardLayout cardLayout;

    public MainFrame(String nombreUsuario) {
        configurarVentana();
        inicializarComponentes(nombreUsuario);
    }

    private void configurarVentana() {
        setTitle(ConfiguracionApp.obtenerNombreAplicacion());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes(String nombreUsuario) {

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(ConfiguracionApp.COLOR_FONDO);

        // ENCABEZADO
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(ConfiguracionApp.COLOR_PRIMARIO);
        panelEncabezado.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("AUTORENTA EXPRESS");
        lblTitulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Bienvenido: " + nombreUsuario);
        lblUsuario.setFont(ConfiguracionApp.FUENTE_NORMAL);
        lblUsuario.setForeground(Color.WHITE);

        panelEncabezado.add(lblTitulo, BorderLayout.WEST);
        panelEncabezado.add(lblUsuario, BorderLayout.EAST);

        // MENÚ LATERAL
        JPanel panelMenu = new JPanel(new GridLayout(7, 1, 5, 10));
        panelMenu.setBackground(new Color(230, 235, 240));
        panelMenu.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10));

        agregarBotonMenu(panelMenu, "Inicio");
        agregarBotonMenu(panelMenu, "Vehículos");
        agregarBotonMenu(panelMenu, "Clientes");
        agregarBotonMenu(panelMenu, "Contratos");
        agregarBotonMenu(panelMenu, "Mantenimientos");
        agregarBotonMenu(panelMenu, "Cerrar sesión");

        // CONTENIDO CON CARDLAYOUT
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(ConfiguracionApp.COLOR_BLANCO);

        // Panel de inicio.
        panelContenido.add(crearPanelInicio(), "Inicio");

        // Paneles de gestión.
        panelContenido.add(new VehiculoPanel(), "Vehículos");
        panelContenido.add(new ClientePanel(), "Clientes");
        panelContenido.add(new ContratoPanel(), "Contratos");
        panelContenido.add(new MantenimientoPanel(), "Mantenimientos");

        // DISTRIBUCIÓN
        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);
        panelPrincipal.add(panelMenu, BorderLayout.WEST);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        add(panelPrincipal);

        cardLayout.show(panelContenido, "Inicio");
    }

    // Crea los botones del menú.
    private void agregarBotonMenu(JPanel panelMenu, String texto) {

        JButton boton = new JButton(texto);
        boton.setFont(ConfiguracionApp.FUENTE_BOTON);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addActionListener(e -> ejecutarOpcion(texto));

        panelMenu.add(boton);
    }

    // Controla la navegación entre los paneles.
    private void ejecutarOpcion(String opcion) {

        if (opcion.equals("Cerrar sesión")) {
            cerrarSesion();
            return;
        }

        cardLayout.show(panelContenido, opcion);
    }

    // Crea la pantalla inicial.
    private JPanel crearPanelInicio() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ConfiguracionApp.COLOR_BLANCO);
        panel.setBorder(
                BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JLabel titulo = new JLabel(
                "PANEL PRINCIPAL DEL SISTEMA",
                SwingConstants.CENTER);

        titulo.setFont(ConfiguracionApp.FUENTE_TITULO);
        titulo.setForeground(ConfiguracionApp.COLOR_PRIMARIO);

        JLabel descripcion = new JLabel(
                "<html><div style='text-align:center;'>"
                + "Bienvenido a AutoRenta Express."
                + "<br><br>Seleccione un módulo del menú lateral "
                + "para comenzar a trabajar."
                + "</div></html>",
                SwingConstants.CENTER);

        descripcion.setFont(ConfiguracionApp.FUENTE_NORMAL);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(descripcion, BorderLayout.CENTER);

        return panel;
    }

    // Cierra la sesión y regresa al login.
    private void cerrarSesion() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
