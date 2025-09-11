package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryptionGateway passwordEncryptionGateway; // Usar la interfaz del dominio
    private final UserValidator userValidator;

    /**
     * Lógica de negocio para registrar un nuevo usuario.
     * @param user El usuario a registrar.
     * @return Un Mono que emite el usuario guardado o un error si las validaciones fallan.
     */
    public Mono<User> registerUser(User user) {
        return userValidator.fieldsValidation(user)
                .flatMap(userValidator::businessRulesValidation)
                .map(userValidated -> {
                    String passwordCodificado = passwordEncryptionGateway.encryptPassword(userValidated.getPassword());
                    return userValidated.toBuilder().password(passwordCodificado).build();
                })
                .flatMap(userRepository::save);
    }
}
