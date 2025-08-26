package co.com.crediya.autenticacion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    // No incluimos el id, ya que lo genera la base de datos.
    private String nombre;
    private String apellido;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private Long idRol;
    private double salarioBase;
}
