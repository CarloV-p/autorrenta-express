/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.autorenta.util;

public final class Validador {

    // @author Marlee Lucero Romero Pequeña

    private Validador() {
    }

    // Comprueba que un texto no sea nulo ni esté vacío.
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    // Comprueba que el DNI tenga exactamente 8 dígitos.
    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{8}");
    }

    // Comprueba que el teléfono tenga entre 9 y 15 dígitos.
    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("\\d{9,15}");
    }

    // Comprueba que un número sea positivo.
    public static boolean esNumeroPositivo(String valor) {
        try {
            double numero = Double.parseDouble(valor.trim());
            return Double.isFinite(numero) && numero > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    // Comprueba que un número sea mayor o igual a cero.
    public static boolean esNumeroNoNegativo(String valor) {
        try {
            double numero = Double.parseDouble(valor.trim());
            return Double.isFinite(numero) && numero >= 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    // Comprueba el formato de una placa, por ejemplo ABC-123.
    public static boolean esPlacaValida(String placa) {
        return placa != null
                && placa.trim().toUpperCase().matches("[A-Z0-9]{3}-[A-Z0-9]{3}");
    }

    // Comprueba que el nombre de usuario tenga al menos 3 caracteres.
    public static boolean esUsuarioValido(String usuario) {
        return usuario != null && usuario.trim().length() >= 3;
    }

    // Comprueba que la contraseña no esté vacía.
    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && !contrasena.isEmpty();
    }

    // Comprueba que un RUC peruano tenga 11 dígitos y empiece con 10 o 20.
    public static boolean esRucValido(String ruc) {
        return ruc != null && ruc.matches("^(10|20)\\d{9}$");
    }

    // Comprueba un formato básico de correo electrónico.
    public static boolean esCorreoValido(String correo) {
        return correo != null
                && correo.trim().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}
