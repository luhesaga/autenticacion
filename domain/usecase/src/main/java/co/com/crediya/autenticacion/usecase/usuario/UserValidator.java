package co.com.crediya.autenticacion.usecase.usuario;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import co.com.crediya.autenticacion.model.usuario.util.Constants;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public Mono<User> fieldsValidation(User user) {
        List<String> errors = new ArrayList<>();

        requiredFieldsValidation(user, errors);

        if (!errors.isEmpty()) {
            return Mono.error(new BusinessValidationException(Constants.ERROR_MESSAGE_REQUIRED_FIELDS, errors));
        }

        return Mono.just(user);
    }

    public Mono<User> businessRulesValidation(User user) {
        return userRepository.findByEmail(user.getEmail())
                .flatMap(userFound -> Mono.error(new BusinessValidationException(Constants.ERROR_EMAIL_ALREADY_REGISTERED)))
                .then(userRepository.findByDocumentId(user.getDocumentId()))
                .flatMap(userFound -> Mono.error(new BusinessValidationException(Constants.ERROR_DOCUMENT_ALREADY_REGISTERED)))
                .then(Mono.just(user));
    }

    private void requiredFieldsValidation(User user, List<String> errors) {

        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            errors.add(Constants.ERROR_NAME_REQUIRED_FIELD);
        }
        if (Objects.isNull(user.getLastname()) || user.getLastname().isBlank()) {
            errors.add(Constants.ERROR_LASTNAME_REQUIRED_FIELD);
        }

        if (user.getSalary() < Constants.MINIMUM_SALARY || user.getSalary() > Constants.MAXIMUM_SALARY) {
            errors.add(Constants.INVALID_SALARY_BASE_MESSAGE);
        }

        if (Objects.isNull(user.getPassword()) || user.getPassword().isBlank()) {
            errors.add(Constants.PASSWORD_REQUIREMENTS_ERROR);
        } else if (!Constants.PASSWORD_COMPLEXITY_PATTERN.matcher(user.getPassword()).matches()) {
            errors.add(Constants.PASSWORD_VALIDATION_ERROR_MESSAGE);
        }

        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            errors.add(Constants.ERROR_EMAIL_REQUIRED_FIELD);
        } else if (!Constants.EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add(Constants.EMAIL_FORMAT_ERROR_MESSAGE);
        }

        if (Objects.isNull(user.getPassword()) ||
                !Constants.PASSWORD_COMPLEXITY_PATTERN.matcher(user.getPassword()).matches()) {
            errors.add(Constants.PASSWORD_VALIDATION_ERROR_MESSAGE);
        }

    }

}
