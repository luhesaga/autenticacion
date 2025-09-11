package co.com.crediya.autenticacion.api.mapper;

import co.com.crediya.autenticacion.api.dto.GenericResponseDTO;
import co.com.crediya.autenticacion.api.dto.UserDTO;
import co.com.crediya.autenticacion.api.dto.UserResponseDTO;
import co.com.crediya.autenticacion.model.usuario.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    @DisplayName("toModel debe mapear todos los campos desde UserDTO hacia User")
    void toModel_mapsAllFields() {
        // Arrange
        LocalDate birthDate = LocalDate.of(1995, 10, 20);
        UserDTO dto = new UserDTO(
                "Juan Carlos",
                "Pérez Gómez",
                "juan.perez@example.com",
                "Contraseña2509e",
                "1140888999",
                "3012345678",
                "Calle 123 25-98",
                birthDate,
                2L,
                2_500_000.0
        );

        // Act
        User model = mapper.toModel(dto);

        // Assert
        assertNotNull(model);
        assertNull(model.getId(), "El id no debe ser establecido por el mapper");
        assertEquals(dto.getName(), model.getName());
        assertEquals(dto.getLastname(), model.getLastname());
        assertEquals(dto.getEmail(), model.getEmail());
        assertEquals(dto.getPassword(), model.getPassword());
        assertEquals(dto.getDocumentId(), model.getDocumentId());
        assertEquals(dto.getPhone(), model.getPhone());
        assertEquals(dto.getAddress(), model.getAddress());
        assertEquals(dto.getBirthDate(), model.getBirthDate());
        assertEquals(dto.getRolId(), model.getRolId());
        assertEquals(dto.getSalary(), model.getSalary(), 0.0001);
        assertNull(model.getRolName(), "El rolName no es parte del DTO y no debe establecerse");
    }

    @Test
    @DisplayName("toSuccessResponse debe construir la respuesta con el código y mensaje esperado y mapear campos del usuario")
    void toSuccessResponse_buildsExpectedResponse() {
        // Arrange
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        User user = User.builder()
                .id(10L)
                .name("Maria")
                .lastname("Lopez")
                .email("maria.lopez@example.com")
                .password("Secreta123")
                .documentId("11223344")
                .phone("3000000000")
                .address("Cra 10 # 20-30")
                .birthDate(birthDate)
                .rolId(3L)
                .rolName("ADMIN")
                .salary(3_000_000.0)
                .build();

        // Act
        GenericResponseDTO<UserResponseDTO> response = mapper.toSuccessResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals("201-001", response.getCode());
        assertEquals("La operacion fue exitosa", response.getMessage());
        assertNotNull(response.getData());

        UserResponseDTO data = response.getData();
        assertEquals(user.getName(), data.getName());
        assertEquals(user.getLastname(), data.getLastname());
        assertEquals(user.getEmail(), data.getEmail());
        assertEquals(user.getDocumentId(), data.getDocumentId());
        assertEquals(user.getPhone(), data.getPhone());
        assertEquals(user.getAddress(), data.getAddress());
        assertEquals(user.getBirthDate(), data.getBirthDate());
        assertEquals(user.getSalary(), data.getSalary(), 0.0001);
    }
}
