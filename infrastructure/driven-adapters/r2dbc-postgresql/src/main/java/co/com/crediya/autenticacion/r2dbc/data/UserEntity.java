package co.com.crediya.autenticacion.r2dbc.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Table("usuario")
public class UserEntity {
    @Id
    @Column("id_usuario")
    private Long id;

    @Column("nombre")
    private String name;

    @Column("apellido")
    private String lastname;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("documento_identidad")
    private String documentId;

    @Column("telefono")
    private String phone;

    @Column("direccion")
    private String address;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("id_rol")
    private Long rolId;

    @Column("salario_base")
    private double salary;

    @ReadOnlyProperty
    @Column("nombre_rol")
    private String rolName;
}
