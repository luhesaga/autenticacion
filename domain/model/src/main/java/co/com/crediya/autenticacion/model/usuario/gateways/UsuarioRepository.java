package co.com.crediya.autenticacion.model.usuario.gateways;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {

    /**
     * Guarda un nuevo usuario en la base de datos.
     * @param usuario El objeto Usuario a guardar.
     * @return Retorna el usuario guardado, envuelto en un Mono.
     */
    Mono<Usuario> guardarUsuario(Usuario usuario);

    /**
     * Busca un usuario por su email para validar si ya existe.
     * @param email El email a buscar.
     * @return Retorna un Mono que puede contener el Usuario si se encuentra, o estar vacío.
     */
    Mono<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su documento de identidad para validar si ya existe.
     * @param documentoIdentidad El documento a buscar.
     * @return Retorna un Mono que puede contener el Usuario si se encuentra, o estar vacío.
     */
    Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
}
