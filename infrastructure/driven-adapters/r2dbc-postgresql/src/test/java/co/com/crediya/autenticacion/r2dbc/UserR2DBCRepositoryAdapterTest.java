package co.com.crediya.autenticacion.r2dbc;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.r2dbc.data.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserR2DBCRepositoryAdapterTest {

    private UserR2DBCDataRepository userR2DBCDataRepository;
    private ReactiveTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        userR2DBCDataRepository = Mockito.mock(UserR2DBCDataRepository.class);
        transactionManager = new FakeReactiveTransactionManager();
    }

    @Test
    void save_success() {
        // Arrange
        User input = sampleUsuario(null).toBuilder().rolName(null).build();
        UserEntity saved = sampleUsuarioData(10L);

        when(userR2DBCDataRepository.save(any(UserEntity.class))).thenReturn(Mono.just(saved));

        UserR2DBCRepositoryAdapter adapter = new UserR2DBCRepositoryAdapter(userR2DBCDataRepository, transactionManager);

        // Act & Assert
        StepVerifier.create(adapter.save(input))
                .assertNext(u -> {
                    assert u.getId().equals(10L);
                    assert u.getName().equals("Juan");
                    assert u.getLastname().equals("Pérez");
                    assert u.getEmail().equals("juan@example.com");
                    assert u.getPassword().equals("secret");
                    assert u.getDocumentId().equals("123");
                    assert u.getPhone().equals("3001234567");
                    assert u.getAddress().equals("Calle 1");
                    assert u.getBirthDate().equals(LocalDate.of(1990, 1, 1));
                    assert u.getRolId().equals(2L);
                    assert u.getSalary() == 1000.0;
                    assert u.getRolName().equals("ADMIN");
                })
                .verifyComplete();
    }

    @Test
    void save_error_propagates() {
        // Arrange
        User input = sampleUsuario(null);
        when(userR2DBCDataRepository.save(any(UserEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        UserR2DBCRepositoryAdapter adapter = new UserR2DBCRepositoryAdapter(userR2DBCDataRepository, transactionManager);

        // Act & Assert
        StepVerifier.create(adapter.save(input))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void findByEmail_found() {
        // Arrange
        UserEntity data = sampleUsuarioData(5L);
        when(userR2DBCDataRepository.findByEmailWithRole("juan@example.com"))
                .thenReturn(Mono.just(data));

        UserR2DBCRepositoryAdapter adapter = new UserR2DBCRepositoryAdapter(userR2DBCDataRepository, transactionManager);

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("juan@example.com"))
                .assertNext(u -> {
                    assert u.getId().equals(5L);
                    assert u.getRolName().equals("ADMIN");
                })
                .verifyComplete();
    }

    @Test
    void findByEmail_notFound() {
        when(userR2DBCDataRepository.findByEmailWithRole("missing@example.com")).thenReturn(Mono.empty());
        UserR2DBCRepositoryAdapter adapter = new UserR2DBCRepositoryAdapter(userR2DBCDataRepository, transactionManager);

        StepVerifier.create(adapter.findByEmail("missing@example.com"))
                .verifyComplete();
    }

    @Test
    void findByDocumento_found() {
        UserEntity data = sampleUsuarioData(7L);
        when(userR2DBCDataRepository.findByDocumentId("123")).thenReturn(Mono.just(data));
        UserR2DBCRepositoryAdapter adapter = new UserR2DBCRepositoryAdapter(userR2DBCDataRepository, transactionManager);

        StepVerifier.create(adapter.findByDocumentId("123"))
                .assertNext(u -> {
                    assert u.getId().equals(7L);
                    assert u.getDocumentId().equals("123");
                })
                .verifyComplete();
    }

    @Test
    void findByDocumento_notFound() {
        when(userR2DBCDataRepository.findByDocumentId("999")).thenReturn(Mono.empty());
        UserR2DBCRepositoryAdapter adapter = new UserR2DBCRepositoryAdapter(userR2DBCDataRepository, transactionManager);

        StepVerifier.create(adapter.findByDocumentId("999")).verifyComplete();
    }

    private static User sampleUsuario(Long id) {
        return User.builder()
                .id(id)
                .name("Juan")
                .lastname("Pérez")
                .email("juan@example.com")
                .password("secret")
                .documentId("123")
                .phone("3001234567")
                .address("Calle 1")
                .birthDate(LocalDate.of(1990, 1, 1))
                .rolId(2L)
                .rolName("ADMIN")
                .salary(1000.0)
                .build();
    }

    private static UserEntity sampleUsuarioData(Long id) {
        UserEntity d = new UserEntity();
        d.setId(id);
        d.setName("Juan");
        d.setLastname("Pérez");
        d.setEmail("juan@example.com");
        d.setPassword("secret");
        d.setDocumentId("123");
        d.setPhone("3001234567");
        d.setAddress("Calle 1");
        d.setBirthDate(LocalDate.of(1990, 1, 1));
        d.setRolId(2L);
        d.setSalary(1000.0);
        // nombreRol es read-only mapeado por JOIN
        try {
            var field = UserEntity.class.getDeclaredField("rolName");
            field.setAccessible(true);
            field.set(d, "ADMIN");
        } catch (Exception ignored) {}
        return d;
    }

    /**
     * Minimal ReactiveTransactionManager that allows TransactionalOperator to work in unit tests
     * without a real database.
     */
    static class FakeReactiveTransactionManager implements ReactiveTransactionManager {
        @Override
        public Mono<ReactiveTransaction> getReactiveTransaction(TransactionDefinition definition) {
            return Mono.just(new ReactiveTransaction() {
                private boolean rollbackOnly = false;
                @Override
                public boolean isNewTransaction() { return true; }
                @Override
                public boolean isRollbackOnly() { return rollbackOnly; }
                @Override
                public void setRollbackOnly() { this.rollbackOnly = true; }
            });
        }
        @Override
        public Mono<Void> commit(ReactiveTransaction transaction) {
            return Mono.empty();
        }
        @Override
        public Mono<Void> rollback(ReactiveTransaction transaction) {
            return Mono.empty();
        }
    }
}
