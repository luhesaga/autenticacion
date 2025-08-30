package co.com.crediya.autenticacion.r2dbc;

import co.com.crediya.autenticacion.r2dbc.data.UsuarioData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

// Extiende de R2dbcRepository, indicando el tipo de entidad y el tipo de la PK
public interface R2DBCRepository extends R2dbcRepository<UsuarioData, Long> {
    // Spring Data implementará este método automáticamente por su nombre
    Mono<UsuarioData> findByEmail(String email);
    Mono<UsuarioData> findByDocumentoIdentidad(String documentoIdentidad);
}
