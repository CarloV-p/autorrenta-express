package pe.edu.utp.autorenta.application.service;

// @author Chirme Ccolque Edwards Alex

import java.util.Objects;
import java.util.Optional;
import pe.edu.utp.autorenta.domain.model.Usuario;
import pe.edu.utp.autorenta.persistence.dao.UsuarioDAO;
import pe.edu.utp.autorenta.util.HashUtil;

// Servicio de login que compara el hash ingresado con el hash guardado del usuario.
public class AutenticacionService {
    private final UsuarioDAO usuarioDAO;

    public AutenticacionService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = Objects.requireNonNull(usuarioDAO, "El UsuarioDAO no puede ser nulo.");
    }

    // Retorna el usuario autenticado solo cuando existe, está activo y la clave coincide.
    public Optional<Usuario> autenticar(String nombreUsuario, String password) {
        if (estaVacio(nombreUsuario) || estaVacio(password)) {
            return Optional.empty();
        }

        String hashIngresado = HashUtil.sha256(password);
        return usuarioDAO.buscarPorNombreUsuario(nombreUsuario.trim())
                .filter(Usuario::isActivo)
                .filter(usuario -> hashIngresado.equals(usuario.getPasswordHash()));
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
