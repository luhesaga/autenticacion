package co.com.crediya.autenticacion.api.mapper;

import co.com.crediya.autenticacion.api.dto.GenericResponseDTO;
import co.com.crediya.autenticacion.api.dto.UserDTO;
import co.com.crediya.autenticacion.api.dto.UserResponseDTO;
import co.com.crediya.autenticacion.model.usuario.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toModel(UserDTO dto) {
        return User.builder()
                .name(dto.getName())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .documentId(dto.getDocumentId())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .birthDate(dto.getBirthDate())
                .rolId(dto.getRolId())
                .salary(dto.getSalary())
                .build();
    }

    public GenericResponseDTO<UserResponseDTO> toSuccessResponse(User user) {
        UserResponseDTO usuarioData = UserResponseDTO.builder()
                .name(user.getName())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .documentId(user.getDocumentId())
                .phone(user.getPhone())
                .address(user.getAddress())
                .birthDate(user.getBirthDate())
                .salary(user.getSalary())
                .build();

        return GenericResponseDTO.<UserResponseDTO>builder()
                .code("201-001")
                .message("La operacion fue exitosa")
                .data(usuarioData)
                .build();
    }
}
