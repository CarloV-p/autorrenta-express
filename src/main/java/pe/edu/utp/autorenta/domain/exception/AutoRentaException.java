package pe.edu.utp.autorenta.domain.exception;
// @author Valdivia Paricahua Carlo Alexander

/*
    excepcion base del sistema
    es no verificada (RuntimeException) para que
    las capas superiores no esten obligadas a declarar "throws" en cada metodo
 */
public class AutoRentaException extends RuntimeException {
    public AutoRentaException(String mensaje){super(mensaje);}

    public AutoRentaException(String mensaje, Throwable causa){super(mensaje, causa);}
}
