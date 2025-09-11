package co.com.crediya.autenticacion.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponseDTO {
    private String name;
    private String lastname;
    private String email;
    private String documentId;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private double salary;
}
