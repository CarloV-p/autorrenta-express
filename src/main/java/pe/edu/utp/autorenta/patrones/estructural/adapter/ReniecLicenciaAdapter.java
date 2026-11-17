package pe.edu.utp.autorenta.patrones.estructural.adapter;

// @author Chirme Ccolque Edwards Alex

import java.util.Objects;
import java.time.LocalDate;
import pe.edu.utp.autorenta.domain.exception.LicenciaInvalidaException;
import pe.edu.utp.autorenta.domain.model.Conductor;
import pe.edu.utp.autorenta.external.reniec.ApiReniecExterna;
import pe.edu.utp.autorenta.external.reniec.ProveedorLicenciaExterno;

// Adapter: convierte la respuesta propia de RENIEC al contrato ValidadorLicencia.
public class ReniecLicenciaAdapter implements ValidadorLicencia {
    // Referencia al contrato externo, no a una implementación específica de RENIEC.
    private final ProveedorLicenciaExterno proveedorLicencia;

    // Crea el adapter con la simulación por defecto de la API RENIEC.
    public ReniecLicenciaAdapter() {
        this(new ApiReniecExterna());
    }

    // Permite inyectar otro proveedor compatible sin modificar este adapter.
    public ReniecLicenciaAdapter(ProveedorLicenciaExterno proveedorLicencia) {
        this.proveedorLicencia = Objects.requireNonNull(proveedorLicencia, "El proveedor de licencias no puede ser nulo.");
    }

    @Override
    public boolean esLicenciaValida(String dni, String nroLicencia) {
        // Extrae solo el dato que necesita el sistema desde la respuesta externa.
        ProveedorLicenciaExterno.ResultadoLicencia resultado = Objects.requireNonNull(
                proveedorLicencia.consultarLicencia(dni, nroLicencia),
                "La respuesta del proveedor de licencias no puede ser nula.");
        return resultado.esVigente();
    }

    @Override
    public void validarLicencia(String dni, String nroLicencia) {
        // Se consulta una única vez para conservar también el motivo de rechazo.
        ProveedorLicenciaExterno.ResultadoLicencia resultado = Objects.requireNonNull(
                proveedorLicencia.consultarLicencia(dni, nroLicencia),
                "La respuesta del proveedor de licencias no puede ser nula.");
        if (!resultado.esVigente()) {
            // El adapter traduce el error del proveedor a una excepción del dominio.
            throw new LicenciaInvalidaException(nroLicencia, resultado.motivo());
        }
    }

    @Override
    public void validarLicencia(Conductor conductor) {
        Objects.requireNonNull(conductor, "El conductor no puede ser nulo.");

        // Se valida primero la fecha conocida por el dominio antes de consultar el proveedor.
        if (!conductor.licenciaVigente(LocalDate.now())) {
            throw new LicenciaInvalidaException(conductor.getNroLicencia(), "está vencida según el registro del conductor");
        }

        // Luego se consulta la API para verificar que la licencia siga habilitada externamente.
        validarLicencia(conductor.getDni(), conductor.getNroLicencia());
    }
}
