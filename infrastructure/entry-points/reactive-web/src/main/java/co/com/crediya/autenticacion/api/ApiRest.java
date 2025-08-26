package co.com.crediya.autenticacion.api;

import co.com.crediya.autenticacion.api.dto.UsuarioDTO;
import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
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
    public Mono<Usuario> registrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return Mono.just(usuarioDTO)
                .map(this::toModel) // 1. Convierte el DTO recibido al modelo de dominio
                .flatMap(usuarioUseCase::registrarUsuario) // 2. Llama al caso de uso
                .onErrorMap(IllegalArgumentException.class, e ->
                        // 3. Maneja errores de validación de negocio
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())
                );
    }
}
