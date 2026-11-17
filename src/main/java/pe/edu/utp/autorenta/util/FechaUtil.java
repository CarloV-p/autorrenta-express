/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



package pe.edu.utp.autorenta.util;

// @author Marlee Lucero Romero Pequeña

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class FechaUtil {

    // @author Marlee Lucero Romero Pequeña

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private FechaUtil() {
    }

    // Obtiene la fecha actual del sistema.
    public static LocalDate obtenerFechaActual() {
        return LocalDate.now();
    }

    // Convierte una fecha a formato dd/MM/yyyy.
    public static String formatearFecha(LocalDate fecha) {
        if (fecha == null) {
            return "";
        }
        return fecha.format(FORMATO);
    }

    // Convierte un texto en una fecha.
    public static LocalDate convertirAFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(fecha.trim(), FORMATO);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Comprueba si una fecha tiene un formato y valor válidos.
    public static boolean esFechaValida(String fecha) {
        return convertirAFecha(fecha) != null;
    }

    // Comprueba si una fecha es posterior a la fecha actual.
    public static boolean esFechaFutura(String fecha) {
        LocalDate fechaConvertida = convertirAFecha(fecha);

        return fechaConvertida != null
                && fechaConvertida.isAfter(LocalDate.now());
    }

    // Comprueba si una fecha es anterior a la fecha actual.
    public static boolean esFechaPasada(String fecha) {
        LocalDate fechaConvertida = convertirAFecha(fecha);

        return fechaConvertida != null
                && fechaConvertida.isBefore(LocalDate.now());
    }

    // Comprueba que la fecha inicial no sea posterior a la final.
    public static boolean esRangoValido(
            LocalDate fechaInicio, LocalDate fechaFin) {

        return fechaInicio != null
                && fechaFin != null
                && !fechaFin.isBefore(fechaInicio);
    }
}
