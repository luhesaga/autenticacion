package co.com.crediya.autenticacion.model.usuario.exception;

import lombok.Getter;
import java.util.Collections;
import java.util.List;

@Getter
public class BusinessValidationException extends RuntimeException {

    private final List<String> errors;

    public BusinessValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public BusinessValidationException(String message) {
        super(message);
        this.errors = Collections.singletonList(message);
    }
}
