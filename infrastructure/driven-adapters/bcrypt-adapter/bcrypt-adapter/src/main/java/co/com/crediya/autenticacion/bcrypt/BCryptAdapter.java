package co.com.crediya.autenticacion.bcrypt;

import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptAdapter implements PasswordEncryptionGateway {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encryptPassword(String passwordPlano) {
        return passwordEncoder.encode(passwordPlano);
    }
}
