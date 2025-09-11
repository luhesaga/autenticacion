package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.LoginRequestDTO;
import co.com.crediya.autenticacion.api.dto.UserDTO;
import co.com.crediya.autenticacion.api.handler.GlobalExceptionHandler;
import co.com.crediya.autenticacion.api.jwt.JwtProvider;
import co.com.crediya.autenticacion.api.mapper.UserMapper;
import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.usecase.usuario.UserUseCase;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource; // Importar
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AuthApiRest.class, excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@Import({AuthApiRestTest.MockBeanConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "jwt.secret=M1_Cl4v3_S3cr3t4_P4r4_T3sts_D3b3_S3r_Muy_L4rg4_y_S3gur4_64_Chars",
        "jwt.expiration.ms=3600000"
})
class AuthApiRestTest {

    @TestConfiguration
    static class MockBeanConfig {
        @Bean public UserUseCase usuarioUseCase() { return Mockito.mock(UserUseCase.class); }
        @Bean public ReactiveAuthenticationManager authenticationManager() { return Mockito.mock(ReactiveAuthenticationManager.class); }
        @Bean public JwtProvider jwtProvider() { return Mockito.mock(JwtProvider.class); }
        @Bean public UserRepository userRepository() { return Mockito.mock(UserRepository.class); }
        @Bean public UserMapper usuarioApiMapper() { return Mockito.mock(UserMapper.class); }
    }

    @Autowired private WebTestClient webTestClient;
    @Autowired private UserUseCase userUseCase;
    @Autowired private ReactiveAuthenticationManager authenticationManager;
    @Autowired private JwtProvider jwtProvider;
    @Autowired private UserMapper usuarioApiMapper;

    private UserDTO userDTOValido;
    private User userValido;

    @BeforeEach
    void setUp() {
        userValido = User.builder()
                .id(1L).name("Test").lastname("User").email("test@example.com").documentId("12345").phone("300123").address("Calle 123").birthDate(LocalDate.now()).rolId(1L).salary(2000000.0)
                .build();
        userDTOValido = new UserDTO("Test", "User", "test@example.com", "Password123", "12345", "300123", "Calle 123", LocalDate.now(), 1L, 2000000.0);
        when(usuarioApiMapper.toModel(any(UserDTO.class))).thenReturn(userValido);
        when(usuarioApiMapper.toSuccessResponse(any(User.class))).thenReturn(
                co.com.crediya.autenticacion.api.dto.GenericResponseDTO.<co.com.crediya.autenticacion.api.dto.UserResponseDTO>builder()
                        .code("201-001")
                        .message("La operacion fue exitosa")
                        .data(co.com.crediya.autenticacion.api.dto.UserResponseDTO.builder()
                                .name("Test")
                                .lastname("User")
                                .email("test@example.com")
                                .documentId("12345")
                                .phone("300123")
                                .address("Calle 123")
                                .birthDate(LocalDate.now())
                                .salary(2000000.0)
                                .build())
                        .build()
        );
    }

    @Test
    @DisplayName("POST /login debería devolver un token cuando las credenciales son válidas")
    void loginExitoso() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");
        Authentication auth = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(auth));
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("mock_jwt_token");

        webTestClient.post().uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk(); // <-- Ahora pasará
    }

    @Test
    @DisplayName("POST /login debería devolver 401 Unauthorized cuando las credenciales son inválidas")
    void loginFallido() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrong_password");
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(Mono.error(new BadCredentialsException("Credenciales incorrectas")));

        webTestClient.post().uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized(); // <-- Ahora pasará
    }

    @Test
    @DisplayName("POST /usuarios debería devolver 201 Created cuando el usuario es creado")
    void registerUserExitoso() {
        when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(userValido));

        webTestClient.post().uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTOValido)
                .exchange()
                .expectStatus().isCreated(); // <-- Ahora pasará
    }
}