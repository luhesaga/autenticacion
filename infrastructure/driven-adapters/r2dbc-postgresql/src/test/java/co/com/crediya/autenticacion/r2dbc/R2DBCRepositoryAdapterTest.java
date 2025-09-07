package co.com.crediya.autenticacion.r2dbc;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.r2dbc.data.UsuarioData;
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

class R2DBCRepositoryAdapterTest {

    private UsuarioDataRepository usuarioDataRepository;
    private ReactiveTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        usuarioDataRepository = Mockito.mock(UsuarioDataRepository.class);
        transactionManager = new FakeReactiveTransactionManager();
    }

    @Test
    void guardarUsuario_success() {
        // Arrange
        Usuario input = sampleUsuario(null).toBuilder().nombreRol(null).build();
        UsuarioData saved = sampleUsuarioData(10L);

        when(usuarioDataRepository.save(any(UsuarioData.class))).thenReturn(Mono.just(saved));

        R2DBCRepositoryAdapter adapter = new R2DBCRepositoryAdapter(usuarioDataRepository, transactionManager);

        // Act & Assert
        StepVerifier.create(adapter.guardarUsuario(input))
                .assertNext(u -> {
                    assert u.getIdUsuario().equals(10L);
                    assert u.getNombre().equals("Juan");
                    assert u.getApellido().equals("Pérez");
                    assert u.getEmail().equals("juan@example.com");
                    assert u.getPassword().equals("secret");
                    assert u.getDocumentoIdentidad().equals("123");
                    assert u.getTelefono().equals("3001234567");
                    assert u.getDireccion().equals("Calle 1");
                    assert u.getFechaNacimiento().equals(LocalDate.of(1990, 1, 1));
                    assert u.getIdRol().equals(2L);
                    assert u.getSalarioBase() == 1000.0;
                    assert u.getNombreRol().equals("ADMIN");
                })
                .verifyComplete();
    }

    @Test
    void guardarUsuario_error_propagates() {
        // Arrange
        Usuario input = sampleUsuario(null);
        when(usuarioDataRepository.save(any(UsuarioData.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        R2DBCRepositoryAdapter adapter = new R2DBCRepositoryAdapter(usuarioDataRepository, transactionManager);

        // Act & Assert
        StepVerifier.create(adapter.guardarUsuario(input))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void findByEmail_found() {
        // Arrange
        UsuarioData data = sampleUsuarioData(5L);
        when(usuarioDataRepository.findByEmailWithRole("juan@example.com"))
                .thenReturn(Mono.just(data));

        R2DBCRepositoryAdapter adapter = new R2DBCRepositoryAdapter(usuarioDataRepository, transactionManager);

        // Act & Assert
        StepVerifier.create(adapter.findByEmail("juan@example.com"))
                .assertNext(u -> {
                    assert u.getIdUsuario().equals(5L);
                    assert u.getNombreRol().equals("ADMIN");
                })
                .verifyComplete();
    }

    @Test
    void findByEmail_notFound() {
        when(usuarioDataRepository.findByEmailWithRole("missing@example.com")).thenReturn(Mono.empty());
        R2DBCRepositoryAdapter adapter = new R2DBCRepositoryAdapter(usuarioDataRepository, transactionManager);

        StepVerifier.create(adapter.findByEmail("missing@example.com"))
                .verifyComplete();
    }

    @Test
    void findByDocumento_found() {
        UsuarioData data = sampleUsuarioData(7L);
        when(usuarioDataRepository.findByDocumentoIdentidad("123")).thenReturn(Mono.just(data));
        R2DBCRepositoryAdapter adapter = new R2DBCRepositoryAdapter(usuarioDataRepository, transactionManager);

        StepVerifier.create(adapter.findByDocumentoIdentidad("123"))
                .assertNext(u -> {
                    assert u.getIdUsuario().equals(7L);
                    assert u.getDocumentoIdentidad().equals("123");
                })
                .verifyComplete();
    }

    @Test
    void findByDocumento_notFound() {
        when(usuarioDataRepository.findByDocumentoIdentidad("999")).thenReturn(Mono.empty());
        R2DBCRepositoryAdapter adapter = new R2DBCRepositoryAdapter(usuarioDataRepository, transactionManager);

        StepVerifier.create(adapter.findByDocumentoIdentidad("999")).verifyComplete();
    }

    private static Usuario sampleUsuario(Long id) {
        return Usuario.builder()
                .idUsuario(id)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .password("secret")
                .documentoIdentidad("123")
                .telefono("3001234567")
                .direccion("Calle 1")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .idRol(2L)
                .nombreRol("ADMIN")
                .salarioBase(1000.0)
                .build();
    }

    private static UsuarioData sampleUsuarioData(Long id) {
        UsuarioData d = new UsuarioData();
        d.setIdUsuario(id);
        d.setNombre("Juan");
        d.setApellido("Pérez");
        d.setEmail("juan@example.com");
        d.setPassword("secret");
        d.setDocumentoIdentidad("123");
        d.setTelefono("3001234567");
        d.setDireccion("Calle 1");
        d.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        d.setIdRol(2L);
        d.setSalarioBase(1000.0);
        // nombreRol es read-only mapeado por JOIN
        try {
            var field = UsuarioData.class.getDeclaredField("nombreRol");
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
