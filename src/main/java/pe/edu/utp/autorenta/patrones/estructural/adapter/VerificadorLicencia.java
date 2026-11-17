package pe.edu.utp.autorenta.patrones.estructural.adapter;

// @author Chirme Ccolque Edwards Alex

// Interfaz pequeña para clientes que solo necesitan conocer el estado de una licencia.
// Aplica ISP: no obliga a depender de la validación que lanza excepciones.
public interface VerificadorLicencia {

    // Devuelve true cuando el proveedor confirma que la licencia puede usarse.
    boolean esLicenciaValida(String dni, String nroLicencia);
}
