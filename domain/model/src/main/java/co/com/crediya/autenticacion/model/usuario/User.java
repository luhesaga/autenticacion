package co.com.crediya.autenticacion.model.usuario;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String documentId;
    private String phone;
    private String address;
    private LocalDate birthDate;
    private Long rolId;
    private String rolName;
    private double salary;
}
