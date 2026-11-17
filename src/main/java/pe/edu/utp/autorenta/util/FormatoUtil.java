/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pe.edu.utp.autorenta.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class FormatoUtil {

    // @author Marlee Lucero Romero Pequeña

    private static final Locale LOCALE_PERU = new Locale("es", "PE");

    private FormatoUtil() {
    }

    // Convierte un importe a formato de moneda peruana.
    public static String formatearMoneda(double importe) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(LOCALE_PERU);
        return formato.format(importe);
    }

    // Convierte un número a formato decimal con dos cifras.
    public static String formatearDecimal(double numero) {
        return String.format(LOCALE_PERU, "%.2f", numero);
    }

    // Convierte un texto a mayúsculas.
    public static String convertirMayusculas(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase(LOCALE_PERU);
    }

    // Convierte un texto a minúsculas.
    public static String convertirMinusculas(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase(LOCALE_PERU);
    }

    // Elimina espacios al inicio y al final.
    public static String limpiarTexto(String texto) {
        return texto == null ? "" : texto.trim();
    }

    // Muestra un texto cuando el valor está vacío.
    public static String mostrarValor(String valor) {
        return valor == null || valor.trim().isEmpty()
                ? "No especificado"
                : valor.trim();
    }
}
