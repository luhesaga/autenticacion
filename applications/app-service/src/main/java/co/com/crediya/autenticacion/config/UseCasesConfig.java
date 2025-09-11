package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import co.com.crediya.autenticacion.usecase.usuario.UserUseCase;
import co.com.crediya.autenticacion.usecase.usuario.UserValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.autenticacion.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
    @Bean
    public UserValidator userValidator(UserRepository userRepository) {
        return new UserValidator(userRepository);
    }

    @Bean
    public UserUseCase userUseCase(UserRepository userRepository,
                                      PasswordEncryptionGateway passwordEncryptionGateway,
                                      UserValidator userValidator) {
        return new UserUseCase(userRepository, passwordEncryptionGateway, userValidator);
    }

}
