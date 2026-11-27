
package pe.edu.utp.autorenta.vista.swing;

// @author Marlee Lucero Romero Pequeña

import pe.edu.utp.autorenta.config.ConfiguracionApp;
import pe.edu.utp.autorenta.vista.swing.panels.VehiculoPanel;
import pe.edu.utp.autorenta.vista.swing.panels.ClientePanel;
import pe.edu.utp.autorenta.vista.swing.panels.ContratoPanel;
import pe.edu.utp.autorenta.vista.swing.panels.MantenimientoPanel;
import pe.edu.utp.autorenta.vista.swing.panels.CoberturaPanel;
import pe.edu.utp.autorenta.vista.swing.panels.ReportePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {

    private JPanel panelContenido;
    private CardLayout cardLayout;

    private final Runnable accionCerrarSesion;

    private final Color azulOscuro = new Color(20, 42, 68);
    private final Color azulPrincipal = new Color(32, 75, 115);
    private final Color azulClaro = new Color(230, 240, 249);
    private final Color fondo = new Color(245, 248, 252);
    private final Color texto = new Color(45, 58, 73);

    public MainFrame(String nombreUsuario) {
        this(nombreUsuario, () -> {});
    }

    public MainFrame(String nombreUsuario, Runnable accionCerrarSesion) {
        this.accionCerrarSesion = accionCerrarSesion;

        configurarVentana();
        inicializarComponentes(nombreUsuario);
    }

    private void configurarVentana() {

        setTitle(ConfiguracionApp.obtenerNombreAplicacion());
        setSize(1200, 760);
        setMinimumSize(new Dimension(1050, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes(String nombreUsuario) {

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(fondo);

        // ENCABEZADO
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(azulOscuro);
        panelEncabezado.setBorder(
                BorderFactory.createEmptyBorder(17, 25, 17, 25)
        );

        JLabel lblTitulo = new JLabel("AUTORENTA EXPRESS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Usuario: " + nombreUsuario);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(new Color(225, 235, 245));

        panelEncabezado.add(lblTitulo, BorderLayout.WEST);
        panelEncabezado.add(lblUsuario, BorderLayout.EAST);

        // MENÚ LATERAL
        JPanel panelMenu = new JPanel(
                new GridLayout(8, 1, 0, 10)
        );

        panelMenu.setPreferredSize(new Dimension(205, 0));
        panelMenu.setBackground(new Color(235, 241, 247));
        panelMenu.setBorder(
                BorderFactory.createEmptyBorder(25, 12, 25, 12)
        );

        agregarBotonMenu(panelMenu, "⌂   Inicio");
        agregarBotonMenu(panelMenu, "▣   Vehículos");
        agregarBotonMenu(panelMenu, "♙   Clientes");
        agregarBotonMenu(panelMenu, "▤   Contratos");
        agregarBotonMenu(panelMenu, "⚙   Mantenimientos");
        agregarBotonMenu(panelMenu, "◇   Coberturas");
        agregarBotonMenu(panelMenu, "▥   Reportes");
        agregarBotonMenu(panelMenu, "↪   Cerrar sesión");

        // CONTENIDO CENTRAL
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(fondo);

        panelContenido.add(crearPanelInicio(nombreUsuario), "Inicio");

        panelContenido.add(new VehiculoPanel(), "Vehículos");
        panelContenido.add(new ClientePanel(), "Clientes");
        panelContenido.add(new ContratoPanel(), "Contratos");
        panelContenido.add(new MantenimientoPanel(), "Mantenimientos");
        panelContenido.add(new CoberturaPanel(), "Coberturas");
        panelContenido.add(new ReportePanel(), "Reportes");

        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);
        panelPrincipal.add(panelMenu, BorderLayout.WEST);
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        add(panelPrincipal);

        cardLayout.show(panelContenido, "Inicio");
    }

    // BOTONES DEL MENÚ
    private void agregarBotonMenu(JPanel panelMenu, String textoBoton) {

        JButton boton = new JButton(textoBoton);

        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(azulOscuro);
        boton.setBackground(Color.WHITE);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFocusPainted(false);
        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 229, 237)
                        ),
                        BorderFactory.createEmptyBorder(10, 12, 10, 8)
                )
        );
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(azulClaro);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(Color.WHITE);
            }
        });

        boton.addActionListener(e -> ejecutarOpcion(textoBoton));

        panelMenu.add(boton);
    }

    // NAVEGACIÓN ENTRE MÓDULOS
    private void ejecutarOpcion(String opcion) {

        if (opcion.contains("Cerrar sesión")) {
            cerrarSesion();
            return;
        }

        String nombrePanel = opcion.substring(opcion.indexOf(" ") + 1).trim();

        cardLayout.show(panelContenido, nombrePanel);
    }

    // PANTALLA PRINCIPAL
    private JPanel crearPanelInicio(String nombreUsuario) {

        JPanel panel = new JPanel(new BorderLayout(20, 25));
        panel.setBackground(fondo);
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        // TÍTULO DE BIENVENIDA
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel titulo = new JLabel("¡Bienvenido a AutoRenta Express!");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titulo.setForeground(azulOscuro);

        JLabel subtitulo = new JLabel(
                "Sistema de gestión y administración de alquiler de vehículos"
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(105, 120, 135));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(8));
        textos.add(subtitulo);

        panelSuperior.add(textos, BorderLayout.WEST);

        // BANNER PRINCIPAL
        JPanel banner = new JPanel(new BorderLayout(15, 10)) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                GradientPaint degradado = new GradientPaint(
                        0, 0, azulOscuro,
                        getWidth(), getHeight(), azulPrincipal
                );

                g2.setPaint(degradado);

                g2.fill(
                        new RoundRectangle2D.Float(
                                0, 0, getWidth(), getHeight(), 25, 25
                        )
                );

                g2.dispose();
            }
        };

        banner.setOpaque(false);
        banner.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 25)
        );

        JPanel textoBanner = new JPanel();
        textoBanner.setOpaque(false);
        textoBanner.setLayout(new BoxLayout(textoBanner, BoxLayout.Y_AXIS));

        JLabel tituloBanner = new JLabel("Tu flota, bajo control");
        tituloBanner.setFont(new Font("Segoe UI", Font.BOLD, 25));
        tituloBanner.setForeground(Color.WHITE);

        JLabel descripcionBanner = new JLabel(
                "<html>Administra vehículos, clientes, contratos y servicios "
                + "<br>desde una sola plataforma.</html>"
        );

        descripcionBanner.setFont(
                new Font("Segoe UI", Font.PLAIN, 15)
        );
        descripcionBanner.setForeground(new Color(225, 237, 247));

        JLabel usuarioBanner = new JLabel("Sesión iniciada: " + nombreUsuario);
        usuarioBanner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        usuarioBanner.setForeground(new Color(190, 218, 240));

        textoBanner.add(tituloBanner);
        textoBanner.add(Box.createVerticalStrut(12));
        textoBanner.add(descripcionBanner);
        textoBanner.add(Box.createVerticalStrut(18));
        textoBanner.add(usuarioBanner);

        banner.add(textoBanner, BorderLayout.WEST);

        // ILUSTRACIÓN DEL VEHÍCULO
        banner.add(new IlustracionVehiculo(), BorderLayout.EAST);

        // TARJETAS INFORMATIVAS
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 3, 18, 0));
        panelTarjetas.setOpaque(false);

        panelTarjetas.add(
                crearTarjeta("VEHÍCULOS", "Gestión de flota",
                        "Registra y administra los vehículos disponibles.",
                        "▣")
        );

        panelTarjetas.add(
                crearTarjeta("CLIENTES", "Atención al cliente",
                        "Consulta y administra la información de clientes.",
                        "♙")
        );

        panelTarjetas.add(
                crearTarjeta("CONTRATOS", "Alquileres",
                        "Gestiona contratos y operaciones de alquiler.",
                        "▤")
        );

        // INFORMACIÓN INFERIOR
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);

        JLabel informacion = new JLabel(
                "Seleccione una opción del menú lateral para comenzar."
        );

        informacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        informacion.setForeground(new Color(110, 125, 140));

        pie.add(informacion, BorderLayout.WEST);

        // ORGANIZACIÓN DEL CONTENIDO
        JPanel centro = new JPanel(new BorderLayout(0, 22));
        centro.setOpaque(false);

        centro.add(banner, BorderLayout.NORTH);
        centro.add(panelTarjetas, BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    // TARJETAS DEL PANEL PRINCIPAL
    private JPanel crearTarjeta(
            String etiqueta,
            String titulo,
            String descripcion,
            String icono
    ) {

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 232, 239)
                        ),
                        BorderFactory.createEmptyBorder(20, 18, 20, 18)
                )
        );

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Symbol", Font.BOLD, 31));
        lblIcono.setForeground(azulPrincipal);
        lblIcono.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEtiqueta.setForeground(new Color(110, 130, 150));
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitulo.setForeground(azulOscuro);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescripcion = new JLabel(
                "<html><div style='width:190px;'>"
                + descripcion
                + "</div></html>"
        );

        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(texto);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        tarjeta.add(lblIcono);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(lblEtiqueta);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblDescripcion);

        return tarjeta;
    }

    // ILUSTRACIÓN DEL AUTOMÓVIL DIBUJADA CON JAVA
    private class IlustracionVehiculo extends JPanel {

        public IlustracionVehiculo() {
            setOpaque(false);
            setPreferredSize(new Dimension(280, 155));
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int x = 15;
            int y = 75;

            // SOMBRA
            g2.setColor(new Color(10, 25, 42, 75));
            g2.fillOval(x + 15, y + 48, 220, 22);

            // PARTE SUPERIOR DEL VEHÍCULO
            Polygon techo = new Polygon();

            techo.addPoint(x + 55, y + 15);
            techo.addPoint(x + 85, y - 15);
            techo.addPoint(x + 165, y - 15);
            techo.addPoint(x + 195, y + 15);

            g2.setColor(new Color(115, 181, 218));
            g2.fillPolygon(techo);

            // VENTANAS
            Polygon ventana = new Polygon();

            ventana.addPoint(x + 90, y - 9);
            ventana.addPoint(x + 160, y - 9);
            ventana.addPoint(x + 184, y + 13);
            ventana.addPoint(x + 70, y + 13);

            g2.setColor(new Color(27, 57, 85));
            g2.fillPolygon(ventana);

            // CARROCERÍA
            g2.setColor(new Color(220, 235, 245));

            g2.fillRoundRect(
                    x + 15, y + 12, 235, 48, 25, 25
            );

            // DETALLES DE LA CARROCERÍA
            g2.setColor(new Color(120, 170, 205));
            g2.fillRoundRect(
                    x + 20, y + 18, 225, 5, 5, 5
            );

            // FAROS
            g2.setColor(new Color(255, 225, 130));
            g2.fillRoundRect(
                    x + 237, y + 29, 12, 10, 5, 5
            );

            g2.setColor(new Color(220, 90, 90));
            g2.fillRoundRect(
                    x + 16, y + 29, 10, 10, 5, 5
            );

            // RUEDAS
            g2.setColor(new Color(25, 32, 42));
            g2.fillOval(x + 48, y + 45, 43, 43);
            g2.fillOval(x + 175, y + 45, 43, 43);

            // CENTRO DE RUEDAS
            g2.setColor(new Color(180, 200, 215));
            g2.fillOval(x + 60, y + 57, 19, 19);
            g2.fillOval(x + 187, y + 57, 19, 19);

            g2.setColor(new Color(90, 115, 135));
            g2.fillOval(x + 66, y + 63, 7, 7);
            g2.fillOval(x + 193, y + 63, 7, 7);

            g2.dispose();
        }
    }

    // CERRAR SESIÓN
    private void cerrarSesion() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            dispose();

            accionCerrarSesion.run();
        }
    }
}
