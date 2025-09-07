package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UseCasesConfig.class)
@Import(UseCasesConfigTest.TestConfig.class)
public class UseCasesConfigTest {

    @Autowired
    private UsuarioUseCase usuarioUseCase;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UsuarioRepository usuarioRepository() {
            return Mockito.mock(UsuarioRepository.class);
        }

        @Bean
        public PasswordEncryptionGateway passwordEncryptionGateway() {
            return Mockito.mock(PasswordEncryptionGateway.class);
        }
    }

    @Test
    void testUseCaseBeansExist() {
        assertNotNull(usuarioUseCase, "El bean usuarioUseCase no debería ser nulo");
    }
}