package co.com.crediya.autenticacion.api.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class CustomAccessDeniedHandlerTest {

    private final CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("handle debe responder 403, application/json y un mensaje en el cuerpo")
    void handle_returnsForbiddenWithJsonBody() throws Exception {
        // Arrange
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/recurso-protegido").build()
        );
        AccessDeniedException denied = new AccessDeniedException("Sin permisos");

        // Act
        Mono<Void> result = handler.handle(exchange, denied);
        // ensure completion
        result.block();

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        String body = exchange.getResponse().getBodyAsString().block();
        assertNotNull(body);
        JsonNode json = mapper.readTree(body);
        assertTrue(json.has("message"));
        assertEquals("El usuario no tiene los permisos necesarios para realizar esta acción.", json.get("message").asText());
    }
}
