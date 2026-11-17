/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.config;

import java.awt.Color;
import java.awt.Font;

public final class ConfiguracionApp {

    // @author Marlee Lucero Romero Pequeña

    public static final String NOMBRE_APLICACION = "AutoRenta Express";
    public static final String VERSION = "1.0";

    // Colores principales de la aplicación.
    public static final Color COLOR_PRIMARIO = new Color(31, 78, 121);
    public static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    public static final Color COLOR_FONDO = new Color(245, 247, 250);
    public static final Color COLOR_TEXTO = new Color(44, 62, 80);
    public static final Color COLOR_EXITO = new Color(39, 174, 96);
    public static final Color COLOR_ERROR = new Color(231, 76, 60);
    public static final Color COLOR_BLANCO = Color.WHITE;

    // Fuentes principales.
    public static final Font FUENTE_TITULO =
            new Font("Arial", Font.BOLD, 22);

    public static final Font FUENTE_SUBTITULO =
            new Font("Arial", Font.BOLD, 16);

    public static final Font FUENTE_NORMAL =
            new Font("Arial", Font.PLAIN, 14);

    public static final Font FUENTE_BOTON =
            new Font("Arial", Font.BOLD, 13);

    private ConfiguracionApp() {
    }

    // Devuelve el nombre completo de la aplicación.
    public static String obtenerNombreAplicacion() {
        return NOMBRE_APLICACION + " v" + VERSION;
    }
}
