package co.com.crediya.autenticacion.r2dbc;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.r2dbc.data.UsuarioData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository // Le dice a Spring que esta clase es un componente de persistencia
@RequiredArgsConstructor // Inyecta las dependencias finales
@Slf4j
public class R2DBCRepositoryAdapter implements UsuarioRepository {
    private final R2DBCRepository r2dbcRepository; // Repositorio de Spring Data
    private final ReactiveTransactionManager transactionManager;

    // Mapeador simple.
    private Usuario toModel(UsuarioData data) {
        return Usuario.builder()
                .idUsuario(data.getIdUsuario())
                .nombre(data.getNombre())
                .apellido(data.getApellido())
                .email(data.getEmail())
                .password(data.getPassword())
                .documentoIdentidad(data.getDocumentoIdentidad())
                .telefono(data.getTelefono())
                .direccion(data.getDireccion())
                .fechaNacimiento(data.getFechaNacimiento())
                .idRol(data.getIdRol())
                .salarioBase(data.getSalarioBase())
                .build();
    }

    private UsuarioData toData(Usuario model) {
        UsuarioData data = new UsuarioData();
        data.setIdUsuario(model.getIdUsuario());
        data.setNombre(model.getNombre());
        data.setApellido(model.getApellido());
        data.setEmail(model.getEmail());
        data.setPassword(model.getPassword());
        data.setDocumentoIdentidad(model.getDocumentoIdentidad());
        data.setTelefono(model.getTelefono());
        data.setDireccion(model.getDireccion());
        data.setFechaNacimiento(model.getFechaNacimiento());
        data.setIdRol(model.getIdRol());
        data.setSalarioBase(model.getSalarioBase());
        return data;
    }

    @Override
    public Mono<Usuario> guardarUsuario(Usuario usuario) {
        TransactionalOperator operator = TransactionalOperator.create(transactionManager); // operador transaccional reactivo
        log.trace("[R2DBCRepositoryAdapter] Guardando usuario email={}", usuario.getEmail());
        return Mono.just(usuario)
                .map(this::toData)
                .flatMap(r2dbcRepository::save)
                .map(this::toModel)
                .doOnSuccess(u -> log.debug("[R2DBCRepositoryAdapter] Usuario guardado id={}, email={}", u.getIdUsuario(), u.getEmail()))
                .doOnError(e -> log.warn("[R2DBCRepositoryAdapter] Error guardando usuario {}: {}", usuario.getEmail(), e.getMessage(), e))
                .as(operator::transactional); // <--- garantizar la atomicidad de la operación de guardado
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        log.trace("[R2DBCRepositoryAdapter] Buscando usuario por email={}", email);
        return r2dbcRepository.findByEmail(email) // Llama al método del repo de Spring
                .map(this::toModel) // Convierte el resultado al modelo de dominio
                .doOnSuccess(u -> {
                    if (u != null) {
                        log.debug("[R2DBCRepositoryAdapter] Usuario encontrado id={}, email={}", u.getIdUsuario(), email);
                    } else {
                        log.debug("[R2DBCRepositoryAdapter] No se encontró usuario con email={}", email);
                    }
                })
                .doOnError(e -> log.warn("[R2DBCRepositoryAdapter] Error buscando email {}: {}", email, e.getMessage(), e));
    }

    @Override
    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        log.trace("[R2DBCRepositoryAdapter] Buscando usuario por documento={}", documentoIdentidad);
        return r2dbcRepository.findByDocumentoIdentidad(documentoIdentidad) // Llama al método del repo de Spring
                .map(this::toModel) // Convierte el resultado al modelo de dominio
                .doOnSuccess(u -> {
                    if (u != null) {
                        log.debug("[R2DBCRepositoryAdapter] Usuario encontrado id={}, documento={}", u.getIdUsuario(), documentoIdentidad);
                    } else {
                        log.debug("[R2DBCRepositoryAdapter] No se encontró usuario con documento={}", documentoIdentidad);
                    }
                })
                .doOnError(e -> log.warn("[R2DBCRepositoryAdapter] Error buscando documento {}: {}", documentoIdentidad, e.getMessage(), e));
    }
}
