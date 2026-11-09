package pe.edu.utp.autorenta.domain.exception;
// @author Valdivia Paricahua Carlo Alexander

/*
    envuelve cualquier SQLException para que las capas de negocio y vista
    NUNCA dependan de java.sql (oculta los detalles de persistencia)
 */
public class PersistenciaException extends AutoRentaException{
    public PersistenciaException(String mensaje, Throwable causa){super(mensaje, causa);}
}
