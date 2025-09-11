package co.com.crediya.autenticacion.r2dbc;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import co.com.crediya.autenticacion.r2dbc.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserR2DBCRepositoryAdapter implements UserRepository {
    private final UserR2DBCDataRepository userR2DBCDataRepository;
    private final ReactiveTransactionManager transactionManager;

    @Override
    public Mono<User> save(User user) {
        TransactionalOperator operator = TransactionalOperator.create(transactionManager);
        log.trace("[UserR2DBCRepositoryAdapter] Guardando user email={}", user.getEmail());
        return Mono.just(user)
                .map(UserMapper::toData)
                .flatMap(userR2DBCDataRepository::save)
                .map(UserMapper::toModel)
                .doOnSuccess(u -> log.debug("[UserR2DBCRepositoryAdapter] User guardado id={}, email={}", u.getId(), u.getEmail()))
                .doOnError(e -> log.warn("[UserR2DBCRepositoryAdapter] Error guardando user {}: {}", user.getEmail(), e.getMessage(), e))
                .as(operator::transactional);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.trace("[UserR2DBCRepositoryAdapter] Buscando usuario por email={}", email);
        return userR2DBCDataRepository.findByEmailWithRole(email) // Llama al método del repo de Spring
                .map(UserMapper::toModel)
                .doOnSuccess(u -> {
                    if (u != null) {
                        log.debug("[UserR2DBCRepositoryAdapter] User encontrado id={}, email={}, rol={}", u.getId(), email, u.getRolName());
                    } else {
                        log.debug("[UserR2DBCRepositoryAdapter] No se encontró usuario con email={}", email);
                    }
                })
                .doOnError(e -> log.warn("[UserR2DBCRepositoryAdapter] Error buscando email {}: {}", email, e.getMessage(), e));
    }

    @Override
    public Mono<User> findByDocumentId(String documentId) {
        log.trace("[UserR2DBCRepositoryAdapter] Buscando usuario por documento={}", documentId);
        return userR2DBCDataRepository.findByDocumentId(documentId)
                .map(UserMapper::toModel)
                .doOnSuccess(u -> {
                    if (u != null) {
                        log.debug("[UserR2DBCRepositoryAdapter] User encontrado id={}, documento={}", u.getId(), documentId);
                    } else {
                        log.debug("[UserR2DBCRepositoryAdapter] No se encontró usuario con documento={}", documentId);
                    }
                })
                .doOnError(e -> log.warn("[UserR2DBCRepositoryAdapter] Error buscando documento {}: {}", documentId, e.getMessage(), e));
    }
}
