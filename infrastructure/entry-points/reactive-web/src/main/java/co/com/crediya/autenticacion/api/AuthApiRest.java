package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.*;
import co.com.crediya.autenticacion.api.jwt.JwtProvider;
import co.com.crediya.autenticacion.api.mapper.UserMapper;
import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import co.com.crediya.autenticacion.usecase.usuario.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Gestion de Usuarios", description = "Operaciones para crear y administrar usuarios.")
public class AuthApiRest {
    private final UserUseCase userUseCase;
    private final UserRepository userRepository;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserMapper usuarioApiMapper;

    @PostMapping(path = "/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema con sus datos personales y de salario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente.",
                    content = @Content(schema = @Schema(implementation = GenericResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public Mono<GenericResponseDTO<UserResponseDTO>> registerUser(@RequestBody UserDTO userDTO) {
        return Mono.just(userDTO)
                .map(usuarioApiMapper::toModel)
                .flatMap(userUseCase::registerUser)
                .map(usuarioApiMapper::toSuccessResponse);
    }

    @PostMapping(path = "/login")
    @Operation(summary = "Autenticar usuario y obtener token JWT")
    public Mono<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        return authenticationManager.authenticate(authenticationToken)
                .map(jwtProvider::generateToken)
                .map(LoginResponseDTO::new)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
    }

    @GetMapping("/usuarios/email/{email}")
    @Operation(summary = "Buscar usuario por email", description = "Devuelve los datos de un usuario específico basado en su email.")
    @PreAuthorize("isAuthenticated()")
    public Mono<User> findByEmail(@PathVariable("email") String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User no encontrado")));
    }
}
