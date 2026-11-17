package pe.edu.utp.autorenta.application.service;

// @author Chirme Ccolque Edwards Alex

import java.util.Objects;
import pe.edu.utp.autorenta.domain.model.Conductor;
import pe.edu.utp.autorenta.patrones.estructural.adapter.ValidadorLicencia;

// Servicio de aplicación que separa el flujo de negocio de la API externa.
public class ValidacionLicenciaService {
    private final ValidadorLicencia validadorLicencia;

    public ValidacionLicenciaService(ValidadorLicencia validadorLicencia) {
        this.validadorLicencia = Objects.requireNonNull(validadorLicencia, "El validador de licencia no puede ser nulo.");
    }

    // Valida la vigencia local y el estado que informa el proveedor externo.
    public void validarConductor(Conductor conductor) {
        validadorLicencia.validarLicencia(Objects.requireNonNull(conductor, "El conductor no puede ser nulo."));
    }
}
