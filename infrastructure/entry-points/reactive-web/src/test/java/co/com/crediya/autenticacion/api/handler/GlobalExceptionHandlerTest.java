package co.com.crediya.autenticacion.api.handler;

import co.com.crediya.autenticacion.api.dto.ErrorDTO;
import co.com.crediya.autenticacion.api.dto.ErrorResponseDTO;
import co.com.crediya.autenticacion.api.error.ApiError;
import co.com.crediya.autenticacion.model.usuario.exception.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleBusinessValidationException debe responder 400 con código y lista de errores")
    void handleBusinessValidationException_returnsBadRequest() {
        // Arrange
        String msg = "Email ya registrado";
        BusinessValidationException ex = new BusinessValidationException(msg);

        // Act
        var response = handler.handleBusinessValidationException(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiError.INVALID_DATA.getCode(), body.getCode());
        assertEquals(ApiError.INVALID_DATA.getMessage(), body.getMessage());
        assertNotNull(body.getErrors());
        assertEquals(1, body.getErrors().size());
        assertEquals(msg, body.getErrors().get(0));
    }

    @Test
    @DisplayName("handleResponseStatusException debe propagar el status y el reason como mensaje")
    void handleResponseStatusException_propagatesStatusAndReason() {
        // Arrange
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "User no encontrado");

        // Act
        var response = handler.handleResponseStatusException(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("User no encontrado", body.getMessage());
    }

    @Test
    @DisplayName("handleGenericException debe responder 500 con código y mensaje genérico")
    void handleGenericException_returnsInternalServerError() {
        // Arrange
        Exception ex = new RuntimeException("Causa interna");

        // Act
        var response = handler.handleGenericException(ex).block();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(ApiError.UNEXPECTED_ERROR.getCode(), body.getCode());
        assertEquals(ApiError.UNEXPECTED_ERROR.getMessage(), body.getMessage());
        assertNull(body.getErrors());
    }
}
