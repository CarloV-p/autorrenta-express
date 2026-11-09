package pe.edu.utp.autorenta.domain.model;
// @author Valdivia Paricahua Carlo Alexander

import pe.edu.utp.autorenta.domain.exception.DatosInvalidosException;

/*
    reglas de validación reutilizadas por las entidades del dominio
    es de paquete (package-private): solo las entidades de domain.model la usan
 */
final class ValidacionDominio {
    private ValidacionDominio(){}

    static String texto(String valor, String campo){
        if(valor == null || valor.isBlank()){
            throw new DatosInvalidosException("El campo '" + campo + "' es obligatorio.");
        }
        return valor.trim();
    }

    static <T> T noNulo(T valor, String campo){
        if(valor == null){
            throw new DatosInvalidosException("El campo '" + campo + "' es obligatorio.");
        }
        return valor;
    }

    static double positivo(double valor, String campo){
        if(valor <= 0){
            throw new DatosInvalidosException("El campo '" + campo + "' debe ser mayor que cero.");
        }
        return valor;
    }

    static double noNegativo(double valor, String campo){
        if(valor < 0){
            throw new DatosInvalidosException("El campo '" + campo + "' no puede ser negativo.");
        }
        return valor;
    }

    static String dni(String valor){
        String limpio = texto(valor, "DNI");
        if(!limpio.matches("\\d{8}")){
            throw new DatosInvalidosException("El DNI debe tener exactamente 8 dígitos.");
        }
        return limpio;
    }
}
