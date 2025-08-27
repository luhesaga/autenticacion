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
public class Usuario {
    private Long idUsuario; // Llave primaria (PK)
    private String nombre;
    private String apellido;
    private String email; // Debe ser único
    private String documentoIdentidad;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Long idRol;
    private double salarioBase;
}
