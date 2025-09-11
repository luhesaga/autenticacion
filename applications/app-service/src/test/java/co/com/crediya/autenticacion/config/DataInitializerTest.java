package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.gateways.PasswordEncryptionGateway;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    private UserRepository userRepository;
    private PasswordEncryptionGateway passwordEncryptionGateway;
    private DataInitializer dataInitializer;

    // Default admin properties used by the initializer
    private final String adminEmail = "admin@crediya.com";
    private final String adminPassword = "Admin#123";
    private final String adminName = "Admin";
    private final String adminLastname = "Credi";
    private final String adminDocumentId = "123456789";
    private final Long adminRolId = 1L;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncryptionGateway = mock(PasswordEncryptionGateway.class);
        dataInitializer = new DataInitializer(userRepository, passwordEncryptionGateway);

        // Inject @Value fields using reflection
        ReflectionTestUtils.setField(dataInitializer, "adminEmail", adminEmail);
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", adminPassword);
        ReflectionTestUtils.setField(dataInitializer, "adminName", adminName);
        ReflectionTestUtils.setField(dataInitializer, "adminLastname", adminLastname);
        ReflectionTestUtils.setField(dataInitializer, "adminDocumentId", adminDocumentId);
        ReflectionTestUtils.setField(dataInitializer, "adminRolId", adminRolId);
    }

    @Test
    @DisplayName("Creates default admin when it does not exist")
    void createsDefaultAdminWhenNotExists() {
        // Given repository has no user with that email
        when(userRepository.findByEmail(adminEmail)).thenReturn(Mono.empty());
        when(passwordEncryptionGateway.encryptPassword(adminPassword)).thenReturn("ENC_PASS");

        // And saving returns a persisted user
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            // Simulate DB assigning ID
            return Mono.just(u.toBuilder().id(99L).build());
        });

        // When
        ApplicationArguments args = mock(ApplicationArguments.class);
        dataInitializer.run(args);

        // Then: encrypt called and save called with expected user
        verify(passwordEncryptionGateway, timeout(500)).encryptPassword(adminPassword);
        verify(userRepository, timeout(500)).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isEqualTo(adminEmail);
        assertThat(saved.getName()).isEqualTo(adminName);
        assertThat(saved.getLastname()).isEqualTo(adminLastname);
        assertThat(saved.getDocumentId()).isEqualTo(adminDocumentId);
        assertThat(saved.getRolId()).isEqualTo(adminRolId);
        assertThat(saved.getPassword()).isEqualTo("ENC_PASS");
    }

    @Test
    @DisplayName("Does not create default admin when it already exists")
    void doesNotCreateWhenAdminExists() {
        // Given repository returns an existing user
        User existing = User.builder().id(1L).email(adminEmail).build();
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(existing));

        // When
        ApplicationArguments args = mock(ApplicationArguments.class);
        dataInitializer.run(args);

        // Then: neither encrypt nor save should be called
        verify(passwordEncryptionGateway, after(300).never()).encryptPassword(anyString());
        verify(userRepository, after(300).never()).save(any());
    }
}
