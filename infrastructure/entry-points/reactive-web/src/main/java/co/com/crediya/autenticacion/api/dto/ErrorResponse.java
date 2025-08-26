package co.com.crediya.autenticacion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String tipo;
    private String mensaje;
}
