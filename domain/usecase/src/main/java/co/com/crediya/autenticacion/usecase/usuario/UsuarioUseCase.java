package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.Usuario;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.model.usuario.util.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncryptionGateway passwordEncryptionGateway; // Usar la interfaz del dominio

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
                .flatMap(this::validarDocumentoIdentidad)
                .map(u -> {
                    String passwordCodificado = passwordEncryptionGateway.encriptar(u.getPassword());
                    return u.toBuilder().password(passwordCodificado).build();
                })
                .flatMap(usuarioRepository::guardarUsuario);
    }

    private Mono<Usuario> validarCamposRequeridos(Usuario usuario) {
        if (Objects.isNull(usuario.getNombre()) || usuario.getNombre().isBlank() || Objects.isNull(usuario.getApellido()) || usuario.getApellido().isBlank() || Objects.isNull(usuario.getEmail()) || usuario.getEmail().isBlank()) {
            return Mono.error(new BusinessValidationException(Constants.ERROR_CAMPOS_OBLIGATORIOS));
        }
        if (Objects.isNull(usuario.getPassword()) ||
                !Constants.PASSWORD_COMPLEXITY_PATTERN.matcher(usuario.getPassword()).matches()) {
            return Mono.error(new BusinessValidationException(Constants.ERROR_PASSWORD_COMPLEJIDAD));
        }
        return Mono.just(usuario);
    }

    private Mono<Usuario> validarFormatoDatos(Usuario usuario) {
        // Validación del formato del email
        if (!Constants.EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
            return Mono.error(new BusinessValidationException(Constants.ERROR_FORMATO_EMAIL_INVALIDO));
        }
        // Validación del rango del salario
        if (usuario.getSalarioBase() < Constants.SALARIO_MINIMO || usuario.getSalarioBase() > Constants.SALARIO_MAXIMO) {
            return Mono.error(new BusinessValidationException(Constants.ERROR_SALARIO_FUERA_DE_RANGO));
        }
        return Mono.just(usuario);
    }

    private Mono<Usuario> validarEmailNoRegistrado(Usuario usuario) {
        return usuarioRepository.findByEmail(usuario.getEmail())
                .hasElement() // Transforma el resultado en Mono<Boolean>: true si encontró algo, false si no.
                .flatMap(emailExiste -> {
                    if (Boolean.TRUE.equals(emailExiste)) {
                        // Si el email existe, lanzamos el error de forma explícita.
                        return Mono.error(new BusinessValidationException(Constants.ERROR_EMAIL_YA_REGISTRADO));
                    }
                    // Si no existe, simplemente continuamos con el usuario original.
                    return Mono.just(usuario);
                });
    }

    private Mono<Usuario> validarDocumentoIdentidad(Usuario usuario) {
        return usuarioRepository.findByDocumentoIdentidad(usuario.getDocumentoIdentidad())
                .hasElement()
                .flatMap( documentoExiste -> {
                   if (Boolean.TRUE.equals(documentoExiste)) {
                       // Si el documento existe, lanzamos el error de forma explicita
                       return Mono.error(new BusinessValidationException(Constants.ERROR_DOCUMENTO_YA_REGISTRADO));
                   }
                    // Si no existe, simplemente continuamos con el usuario original.
                    return Mono.just(usuario);
                });
    }
}
