package pe.edu.utp.autorenta.domain.exception;
// @author Valdivia Paricahua Carlo Alexander

// se lanza cuando la licencia de conducir no es valida o esta vencida
public class LicenciaInvalidaException extends AutoRentaException {
    public LicenciaInvalidaException(String nroLicencia){
        super("La licencia de conducir " + nroLicencia + " no es válida o está vencida.");
    }

    public LicenciaInvalidaException(String nroLicencia, String motivo){
        super("La licencia de conducir " + nroLicencia + " no es válida: " + motivo);
    }
}
