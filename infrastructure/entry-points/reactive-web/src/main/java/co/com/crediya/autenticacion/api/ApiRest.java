package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.ErrorDTO;
import co.com.crediya.autenticacion.api.dto.UsuarioDTO;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Tag(name = "Gestion de Usuarios", description = "Operaciones para crear y administrar usuarios.")
public class ApiRest {
    private final UsuarioUseCase usuarioUseCase;

    // Mapeador simple para convertir DTO a Modelo
    private Usuario toModel(UsuarioDTO dto) {
        return Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .documentoIdentidad(dto.getDocumentoIdentidad())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .fechaNacimiento(dto.getFechaNacimiento())
                .idRol(dto.getIdRol())
                .salarioBase(dto.getSalarioBase())
                .build();
    }

    @PostMapping(path = "/usuarios")
    @ResponseStatus(HttpStatus.CREATED) // Devuelve un código 201 si es exitoso
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema con sus datos personales y de salario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente."),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. email ya existe, campos obligatorios faltantes).",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    })
    public Mono<Usuario> registrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return Mono.just(usuarioDTO)
                .map(this::toModel) // 1. Convierte el DTO recibido al modelo de dominio
                .flatMap(usuarioUseCase::registrarUsuario); // 2. Llama al caso de uso
    }
}
