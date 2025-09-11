package co.com.crediya.autenticacion.api.handler;

import co.com.crediya.autenticacion.api.dto.ErrorDTO;
import co.com.crediya.autenticacion.api.dto.ErrorResponseDTO;
import co.com.crediya.autenticacion.api.error.ApiError;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja las excepciones de negocio (ej. email duplicado, datos inválidos).
     * Estas son las excepciones que lanzamos a propósito desde el UseCase.
     */
    @ExceptionHandler(BusinessValidationException.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleBusinessValidationException(BusinessValidationException ex) { // Cambiado el tipo de excepción
        logger.warn("Excepción de negocio: {}", ex.getMessage());
        ApiError error = ApiError.INVALID_DATA;
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .errors(ex.getErrors())
                .build();
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse)
        );
    }

    /**
     * Maneja excepciones cuando un recurso no se encuentra.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorDTO>> handleResponseStatusException(ResponseStatusException ex) {
        logger.warn("Excepción de estado de respuesta: {}", ex.getMessage());
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return Mono.just(
                ResponseEntity
                        .status(ex.getStatusCode())
                        .body(new ErrorDTO(message))
        );
    }


    /**
     * Para el Manejo de cualquier otra excepción no controlada.
     * (Ej: error de conexión a la BD, NullPointerException, etc.).
     * Le devuelve al cliente un mensaje genérico para no exponer detalles internos.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDTO>> handleGenericException(Exception ex) {
        logger.error("Error inesperado en la aplicación", ex);
        ApiError error = ApiError.UNEXPECTED_ERROR;
        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}
