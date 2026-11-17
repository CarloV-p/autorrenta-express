package pe.edu.utp.autorenta.external.reniec;

// @author Chirme Ccolque Edwards Alex

import java.util.Locale;
import java.util.regex.Pattern;

// Simula el contrato y las respuestas de una API externa de validación.
// En una versión real, aquí se haría la solicitud HTTP al proveedor.
public class ApiReniecExterna implements ProveedorLicenciaExterno {
    // Formato básico de los datos que enviaría el sistema al servicio externo.
    private static final Pattern DNI_PERUANO = Pattern.compile("\\d{8}");
    private static final Pattern LICENCIA_PERUANA = Pattern.compile("[A-Z]\\d{8}");

    // Consulta el estado de una licencia y devuelve una respuesta entendible por el adapter.
    @Override
    public ResultadoLicencia consultarLicencia(String dni, String nroLicencia) {
        // La API rechaza la consulta antes de revisar la licencia si el DNI es incorrecto.
        if (dni == null || !DNI_PERUANO.matcher(dni.trim()).matches()) {
            return new ResultadoLicencia(false, "el DNI debe tener ocho dígitos");
        }

        // Se controla null antes de normalizar el texto de la licencia.
        if (nroLicencia == null) {
            return new ResultadoLicencia(false, "el número de licencia es obligatorio");
        }

        // Normaliza la entrada para evaluar la licencia sin depender de mayúsculas o espacios.
        String licenciaNormalizada = nroLicencia.trim().toUpperCase(Locale.ROOT);
        if (!LICENCIA_PERUANA.matcher(licenciaNormalizada).matches()) {
            return new ResultadoLicencia(false, "el formato de licencia no es válido");
        }

        // En esta simulación, una licencia que termina en 0000 está bloqueada.
        if (licenciaNormalizada.endsWith("0000")) {
            return new ResultadoLicencia(false, "la licencia figura como bloqueada por el proveedor");
        }

        return new ResultadoLicencia(true, "licencia vigente");
    }
}
