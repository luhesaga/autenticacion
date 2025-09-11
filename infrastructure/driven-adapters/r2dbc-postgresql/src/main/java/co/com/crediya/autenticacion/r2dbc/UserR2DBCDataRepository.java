package co.com.crediya.autenticacion.r2dbc;

import co.com.crediya.autenticacion.r2dbc.data.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserR2DBCDataRepository extends ReactiveCrudRepository<UserEntity, Long> {
    // Trae un usuario y, mediante un JOIN, añade el nombre de su rol.
    @Query("SELECT u.*, r.nombre as nombre_rol FROM usuario u JOIN rol r ON u.id_rol = r.id_rol WHERE u.email = :email")
    Mono<UserEntity> findByEmailWithRole(String email);

    // Versión con JOIN para documento_identidad, para que exista la columna alias nombre_rol
    @Query("SELECT u.*, r.nombre as nombre_rol FROM usuario u JOIN rol r ON u.id_rol = r.id_rol WHERE u.documento_identidad = :documentoIdentidad")
    Mono<UserEntity> findByDocumentId(String documentoIdentidad);

    // Si en algún punto se requiere buscar por email sin el rol, se podría mantener este método,
    // pero dado que la entidad tiene el campo read-only nombreRol, es preferible usar la versión con JOIN.
    Mono<UserEntity> findByEmail(String email);
}
