package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    // Expresión regular para una validación básica de email.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    /**
     * Lógica de negocio para registrar un nuevo usuario.
     * @param usuario El usuario a registrar.
     * @return Un Mono que emite el usuario guardado o un error si las validaciones fallan.
     */
    public Mono<Usuario> registrarUsuario(Usuario usuario) {
        return Mono.just(usuario)
                .flatMap(this::validarCamposRequeridos)
                .flatMap(this::validarFormatoDatos)
                .flatMap(this::validarEmailNoRegistrado)
                .flatMap(usuarioRepository::guardarUsuario);
    }

    private Mono<Usuario> validarCamposRequeridos(Usuario usuario) {
        if (Objects.isNull(usuario.getNombre()) || usuario.getNombre().isBlank() || Objects.isNull(usuario.getApellido()) || usuario.getApellido().isBlank() || Objects.isNull(usuario.getEmail()) || usuario.getEmail().isBlank()) {
            return Mono.error(new IllegalArgumentException("Los campos nombres, apellidos, email y salario_base son obligatorios."));
        }
        return Mono.just(usuario);
    }

    private Mono<Usuario> validarFormatoDatos(Usuario usuario) {
        // Validación del formato del email
        if (!EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
            return Mono.error(new IllegalArgumentException("El formato del email no es válido."));
        }
        // Validación del rango del salario
        if (usuario.getSalarioBase() < 0 || usuario.getSalarioBase() > 15000000) {
            return Mono.error(new IllegalArgumentException("El salario_base debe estar entre 0 y 15,000,000."));
        }
        return Mono.just(usuario);
    }

    private Mono<Usuario> validarEmailNoRegistrado(Usuario usuario) {
        return usuarioRepository.findByEmail(usuario.getEmail())
                .hasElement() // Transforma el resultado en Mono<Boolean>: true si encontró algo, false si no.
                .flatMap(emailExiste -> {
                    if (Boolean.TRUE.equals(emailExiste)) {
                        // Si el email existe, lanzamos el error de forma explícita.
                        return Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado."));
                    }
                    // Si no existe, simplemente continuamos con el usuario original.
                    return Mono.just(usuario);
                });
    }
}
