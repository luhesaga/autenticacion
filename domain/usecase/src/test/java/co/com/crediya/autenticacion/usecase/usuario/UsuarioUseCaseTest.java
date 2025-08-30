package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
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

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .idUsuario(1L)
                .nombre("Carlos")
                .apellido("Santana")
                .documentoIdentidad("12345")
                .email("carlos.santana@test.com")
                .salarioBase(2000000)
                .build();
    }

    @Test
    @DisplayName("Debería registrar un usuario exitosamente cuando los datos son válidos")
    void deberiaRegistrarUsuarioExitosamente() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.guardarUsuario(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

        // Act
        Mono<Usuario> resultado = usuarioUseCase.registrarUsuario(usuarioValido);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(usuarioGuardado ->
                        usuarioGuardado.getEmail().equals("carlos.santana@test.com"))
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
        Usuario usuarioExistente = Usuario.builder()
                .idUsuario(999L)
                .nombre("Otro")
                .apellido("Usuario")
                .documentoIdentidad("12345")
                .email("otro.usuario@test.com")
                .build();

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(usuarioRepository.findByDocumentoIdentidad(anyString())).thenReturn(Mono.just(usuarioExistente));

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
        // Arrange
        usuarioValido.setNombre(null);

        // Act
        Mono<Usuario> resultado = usuarioUseCase.registrarUsuario(usuarioValido);

        // Assert
        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar excepción si el formato del email es inválido")
    void deberiaLanzarExcepcionSiEmailEsInvalido() {
        // Arrange
        usuarioValido.setEmail("correo-invalido");

        // Act
        Mono<Usuario> resultado = usuarioUseCase.registrarUsuario(usuarioValido);

        // Assert
        StepVerifier.create(resultado)
                .expectError(BusinessValidationException.class)
                .verify();
    }
}
