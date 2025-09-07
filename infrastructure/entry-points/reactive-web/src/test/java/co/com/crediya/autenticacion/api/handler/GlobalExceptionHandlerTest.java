package co.com.crediya.autenticacion.api.handler;

import co.com.crediya.autenticacion.api.dto.ErrorDTO;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleBusinessValidationException debe responder 400 con el mensaje de la excepción")
    void handleBusinessValidationException_returnsBadRequest() {
        // Arrange
        String msg = "Email ya registrado";
        BusinessValidationException ex = new BusinessValidationException(msg);

        // Act
        var response = handler.handleBusinessValidationException(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(msg, body.getMessage());
    }

    @Test
    @DisplayName("handleResponseStatusException debe propagar el status y el reason como mensaje")
    void handleResponseStatusException_propagatesStatusAndReason() {
        // Arrange
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");

        // Act
        var response = handler.handleResponseStatusException(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Usuario no encontrado", body.getMessage());
    }

    @Test
    @DisplayName("handleGenericException debe responder 500 con mensaje genérico")
    void handleGenericException_returnsInternalServerError() {
        // Arrange
        Exception ex = new RuntimeException("Causa interna");

        // Act
        var response = handler.handleGenericException(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Ha ocurrido un error inesperado. Por favor, contacte al soporte.", body.getMessage());
    }
}
