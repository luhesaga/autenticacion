package co.com.crediya.autenticacion.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para la creación de un nuevo usuario en el sistema.")
public class UserDTO {
    @Schema(description = "Nombres del usuario.", example = "Juan Carlos")
    private String name;
    @Schema(description = "Apellidos del usuario.", example = "Pérez Gómez")
    private String lastname;
    @Schema(description = "Correo electrónico único del usuario.", example = "juan.perez@example.com")
    private String email;
    @Schema(description = "Contraseña del usuario, al menos 8 caracteres, al menos una mayúscula y un numero", example = "Contraseña2509e")
    private String password;
    @Schema(description = "Documento de identidad del usuario.", example = "1140888999")
    private String documentId;
    @Schema(description = "Número de teléfono de contacto.", example = "3012345678")
    private String phone;
    @Schema(description = "Dirección de residencia.", example = "Calle 123 25-98")
    private String address;
    @Schema(description = "Fecha de nacimiento del usuario.", example = "1995-10-20")
    private LocalDate birthDate;
    @Schema(description = "Identificador del rol asignado.", example = "2")
    private Long rolId;
    @Schema(description = "Salario base mensual del usuario.", example = "2500000")
    private double salary;
}
