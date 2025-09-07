package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.LoginRequestDTO;
import co.com.crediya.autenticacion.api.dto.UsuarioDTO;
import co.com.crediya.autenticacion.api.handler.GlobalExceptionHandler;
import co.com.crediya.autenticacion.api.jwt.JwtProvider;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.TestPropertySource; // Importar
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ApiRest.class, excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@Import({ApiRestTest.MockBeanConfig.class, GlobalExceptionHandler.class})
// AÑADIMOS ESTA ANOTACIÓN PARA PROVEER LAS PROPIEDADES
@TestPropertySource(properties = {
        "jwt.secret=M1_Cl4v3_S3cr3t4_P4r4_T3sts_D3b3_S3r_Muy_L4rg4_y_S3gur4_64_Chars",
        "jwt.expiration.ms=3600000"
})
class ApiRestTest {

    @TestConfiguration
    static class MockBeanConfig {
        @Bean public UsuarioUseCase usuarioUseCase() { return Mockito.mock(UsuarioUseCase.class); }
        @Bean public ReactiveAuthenticationManager authenticationManager() { return Mockito.mock(ReactiveAuthenticationManager.class); }
        @Bean public JwtProvider jwtProvider() { return Mockito.mock(JwtProvider.class); }
    }

    @Autowired private WebTestClient webTestClient;
    @Autowired private UsuarioUseCase usuarioUseCase;
    @Autowired private ReactiveAuthenticationManager authenticationManager;
    @Autowired private JwtProvider jwtProvider;

    private UsuarioDTO usuarioDTOValido;
    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = Usuario.builder()
                .idUsuario(1L).nombre("Test").apellido("User").email("test@example.com").build();
        usuarioDTOValido = new UsuarioDTO("Test", "User", "test@example.com", "Password123", "12345", "300123", "Calle 123", LocalDate.now(), 1L, 2000000.0);
    }

    @Test
    @DisplayName("POST /login debería devolver un token cuando las credenciales son válidas")
    void loginExitoso() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");
        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(auth));
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("mock_jwt_token");

        // Act & Assert
        webTestClient.post().uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk(); // <-- Ahora pasará
    }

    @Test
    @DisplayName("POST /login debería devolver 401 Unauthorized cuando las credenciales son inválidas")
    void loginFallido() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrong_password");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.error(new BadCredentialsException("Credenciales incorrectas")));

        // Act & Assert
        webTestClient.post().uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized(); // <-- Ahora pasará
    }

    @Test
    @DisplayName("POST /usuarios debería devolver 201 Created cuando el usuario es creado")
    void registrarUsuarioExitoso() {
        // Arrange
        when(usuarioUseCase.registrarUsuario(any(Usuario.class))).thenReturn(Mono.just(usuarioValido));

        // Act & Assert
        webTestClient.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(usuarioDTOValido)
                .exchange()
                .expectStatus().isCreated(); // <-- Ahora pasará
    }
}