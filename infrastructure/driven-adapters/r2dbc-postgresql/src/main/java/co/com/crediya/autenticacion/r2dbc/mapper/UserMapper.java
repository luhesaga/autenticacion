package co.com.crediya.autenticacion.r2dbc.mapper;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.r2dbc.data.UserEntity;

public class UserMapper {

    private UserMapper() {}

    public static User toModel(UserEntity data) {
        return User.builder()
                .id(data.getId())
                .name(data.getName())
                .lastname(data.getLastname())
                .email(data.getEmail())
                .password(data.getPassword())
                .documentId(data.getDocumentId())
                .phone(data.getPhone())
                .address(data.getAddress())
                .birthDate(data.getBirthDate())
                .rolId(data.getRolId())
                .rolName(data.getRolName())
                .salary(data.getSalary())
                .build();
    }

    public static UserEntity toData(User model) {
        UserEntity data = new UserEntity();
        data.setId(model.getId());
        data.setName(model.getName());
        data.setLastname(model.getLastname());
        data.setEmail(model.getEmail());
        data.setPassword(model.getPassword());
        data.setDocumentId(model.getDocumentId());
        data.setPhone(model.getPhone());
        data.setAddress(model.getAddress());
        data.setBirthDate(model.getBirthDate());
        data.setRolId(model.getRolId());
        data.setSalary(model.getSalary());
        return data;
    }
}
