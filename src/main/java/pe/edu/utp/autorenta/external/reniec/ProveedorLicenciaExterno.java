package pe.edu.utp.autorenta.external.reniec;

// @author Chirme Ccolque Edwards Alex

// Contrato mínimo que debe cumplir cualquier proveedor externo de licencias.
// Aplica OCP: se puede agregar otra API sin modificar el adapter.
public interface ProveedorLicenciaExterno {

    // Consulta el estado de la licencia con los datos que identifica el proveedor.
    ResultadoLicencia consultarLicencia(String dni, String nroLicencia);

    // Respuesta inmutable y común que el adapter necesita para decidir el resultado.
    record ResultadoLicencia(boolean esVigente, String motivo) {
    }
}
