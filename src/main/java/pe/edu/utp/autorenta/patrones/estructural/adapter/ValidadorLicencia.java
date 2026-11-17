package pe.edu.utp.autorenta.patrones.estructural.adapter;

// @author Chirme Ccolque Edwards Alex

import pe.edu.utp.autorenta.domain.model.Conductor;

// Contrato para los flujos que deben detenerse si una licencia no es válida.
// Extiende el contrato de consulta para conservar compatibilidad y aplicar ISP.
public interface ValidadorLicencia extends VerificadorLicencia {

    // Valida la licencia y detiene el flujo con una excepción cuando no es válida.
    void validarLicencia(String dni, String nroLicencia);

    // Valida los datos externos y la fecha de vigencia registrada en el conductor.
    void validarLicencia(Conductor conductor);
}
