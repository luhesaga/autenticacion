package co.com.crediya.autenticacion.model.usuario.gateways;

import co.com.crediya.autenticacion.model.usuario.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    /**
     * Guarda un nuevo user en la base de datos.
     * @param user El objeto User a guardar.
     * @return Retorna el user guardado, envuelto en un Mono.
     */
    Mono<User> save(User user);

    /**
     * Busca un usuario por su email para validar si ya existe.
     * @param email El email a buscar.
     * @return Retorna un Mono que puede contener el User si se encuentra, o estar vacío.
     */
    Mono<User> findByEmail(String email);

    /**
     * Busca un usuario por su documento de identidad para validar si ya existe.
     * @param documentoIdentidad El documento a buscar.
     * @return Retorna un Mono que puede contener el User si se encuentra, o estar vacío.
     */
    Mono<User> findByDocumentId(String documentoIdentidad);
}
