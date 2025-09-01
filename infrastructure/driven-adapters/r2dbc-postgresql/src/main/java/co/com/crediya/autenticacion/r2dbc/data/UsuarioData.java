package co.com.crediya.autenticacion.r2dbc.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Table("usuario")
public class UsuarioData {
    @Id
    @Column("id_usuario") // Nombre de la columna PK
    private Long idUsuario;

    @Column("nombre")
    private String nombre;

    @Column("apellido")
    private String apellido;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("telefono")
    private String telefono;

    @Column("direccion")
    private String direccion;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column("id_rol")
    private Long idRol;

    @Column("salario_base")
    private double salarioBase;
}
