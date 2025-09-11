package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono; // Importar Mono

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncryptionGateway passwordEncryptionGateway;

    @Value("${app.default-admin.email}")
    private String adminEmail;
    @Value("${app.default-admin.password}")
    private String adminPassword;
    @Value("${app.default-admin.name}")
    private String adminName;
    @Value("${app.default-admin.lastname}")
    private String adminLastname;
    @Value("${app.default-admin.documentId}")
    private String adminDocumentId;
    @Value("${app.default-admin.rolId}")
    private Long adminRolId;

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByEmail(adminEmail)
                .hasElement()
                .flatMap(adminExists -> {
                    if (!adminExists) {
                        log.info("User administrador por defecto no encontrado. Creando nuevo admin...");
                        User admin = User.builder()
                                .name(adminName)
                                .lastname(adminLastname)
                                .email(adminEmail)
                                .documentId(adminDocumentId)
                                .password(passwordEncryptionGateway.encryptPassword(adminPassword))
                                .rolId(adminRolId)
                                .build();
                        return userRepository.save(admin);
                    } else {
                        log.info("User administrador por defecto ya existe.");
                        return Mono.empty();
                    }
                })
                .doOnSuccess(user -> {
                    if(user != null) {
                        log.info("User administrador {} creado exitosamente.", user.getEmail());
                    }
                })
                .subscribe();
    }
}
