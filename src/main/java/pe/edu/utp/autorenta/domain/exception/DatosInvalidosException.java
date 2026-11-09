package pe.edu.utp.autorenta.domain.exception;
// @author Valdivia Paricahua Carlo Alexander

// se lanza cuando los datos de entrada incumplen una regla del dominio
public class DatosInvalidosException extends AutoRentaException {
    public DatosInvalidosException(String mensaje){super(mensaje);}
}
