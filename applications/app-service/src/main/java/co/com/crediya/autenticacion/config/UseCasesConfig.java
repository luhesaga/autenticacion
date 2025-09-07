package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import co.com.crediya.autenticacion.usecase.usuario.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.autenticacion.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepository usuarioRepository,
                                         PasswordEncryptionGateway passwordEncryptionGateway) {
        return new UsuarioUseCase(usuarioRepository, passwordEncryptionGateway);
    }

}
