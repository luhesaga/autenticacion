package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncryptionGateway passwordEncryptionGateway; // Esencial para probar el UseCase

    private UserUseCase userUseCase;

    private User userValido;

    @BeforeEach
    void setUp() {
        // Construir UserUseCase con un UserValidator real que usa el mock de UserRepository
        UserValidator validator = new UserValidator(userRepository);
        userUseCase = new UserUseCase(userRepository, passwordEncryptionGateway, validator);
        userValido = User.builder()
                .name("Carlos")
                .lastname("Santana")
                .email("carlos.santana@test.com")
                .password("PasswordValido123") // <-- SOLUCIÓN 2: Añadir contraseña válida
                .documentId("12345678")
                .rolId(3L)
                .salary(2000000.0)
                .build();
    }

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void deberiaRegisterUserExitosamente() {
        // Arrange
        // SOLUCIÓN 1: Configurar mocks solo para esta prueba
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.findByDocumentId(anyString())).thenReturn(Mono.empty());
        when(passwordEncryptionGateway.encryptPassword(anyString())).thenReturn("password_encriptado_mock");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userValido.toBuilder().id(100L).build()));

        // Act
        Mono<User> resultado = userUseCase.registerUser(userValido);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(usuario -> usuario.getId() == 100L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el email ya está registrado")
    void deberiaLanzarExcepcionCuandoEmailYaExiste() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(userValido));
        // Asegurar que no haya NPE si por alguna razón continúa el flujo
        lenient().when(userRepository.findByDocumentId(anyString())).thenReturn(Mono.empty());
        lenient().when(passwordEncryptionGateway.encryptPassword(anyString())).thenReturn("password_encriptado_mock");
        lenient().when(userRepository.save(any(User.class))).thenReturn(Mono.just(userValido));

        // Act
        Mono<User> resultado = userUseCase.registerUser(userValido);

        // Assert
        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el documento ya está registrado")
    void deberiaLanzarExcepcionCuandoDocumentoYaExiste() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.findByDocumentId(anyString())).thenReturn(Mono.just(userValido));

        // Act
        Mono<User> resultado = userUseCase.registerUser(userValido);

        // Assert
        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre es nulo")
    void deberiaLanzarExcepcionSiNombreEsNulo() {
        userValido.setName(null);
        StepVerifier.create(userUseCase.registerUser(userValido))
                .expectError(BusinessValidationException.class)
                .verify();
    }
}
