package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import co.com.crediya.autenticacion.usecase.usuario.UserUseCase;
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
    private UserUseCase userUseCase;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserRepository usuarioRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public PasswordEncryptionGateway passwordEncryptionGateway() {
            return Mockito.mock(PasswordEncryptionGateway.class);
        }
    }

    @Test
    void testUseCaseBeansExist() {
        assertNotNull(userUseCase, "El bean userUseCase no debería ser nulo");
    }
}