package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncryptionGateway passwordEncryptionGateway; // Esencial para probar el UseCase

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .nombre("Carlos")
                .apellido("Santana")
                .email("carlos.santana@test.com")
                .password("PasswordValido123") // <-- SOLUCIÓN 2: Añadir contraseña válida
                .documentoIdentidad("12345678")
                .idRol(3L)
                .salarioBase(2000000.0)
                .build();
    }

    @Test
    @DisplayName("Debería registrar un usuario exitosamente")
    void deberiaRegistrarUsuarioExitosamente() {
        // Arrange
        // SOLUCIÓN 1: Configurar mocks solo para esta prueba
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(anyString())).thenReturn(Mono.empty());
        when(passwordEncryptionGateway.encriptar(anyString())).thenReturn("password_encriptado_mock");
        when(usuarioRepository.guardarUsuario(any(Usuario.class))).thenReturn(Mono.just(usuarioValido.toBuilder().idUsuario(100L).build()));

        // Act
        Mono<Usuario> resultado = usuarioUseCase.registrarUsuario(usuarioValido);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(usuario -> usuario.getIdUsuario() == 100L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el email ya está registrado")
    void deberiaLanzarExcepcionCuandoEmailYaExiste() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.just(usuarioValido));

        // Act
        Mono<Usuario> resultado = usuarioUseCase.registrarUsuario(usuarioValido);

        // Assert
        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el documento ya está registrado")
    void deberiaLanzarExcepcionCuandoDocumentoYaExiste() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(anyString())).thenReturn(Mono.just(usuarioValido));

        // Act
        Mono<Usuario> resultado = usuarioUseCase.registrarUsuario(usuarioValido);

        // Assert
        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre es nulo")
    void deberiaLanzarExcepcionSiNombreEsNulo() {
        usuarioValido.setNombre(null);
        StepVerifier.create(usuarioUseCase.registrarUsuario(usuarioValido))
                .expectError(BusinessValidationException.class)
                .verify();
    }
}
