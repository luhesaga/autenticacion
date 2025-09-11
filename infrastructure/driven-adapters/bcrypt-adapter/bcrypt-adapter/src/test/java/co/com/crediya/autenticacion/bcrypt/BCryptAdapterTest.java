package co.com.crediya.autenticacion.bcrypt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BCryptAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BCryptAdapter bCryptAdapter;

    @Test
    @DisplayName("Debería llamar al método encode del PasswordEncoder")
    void deberiaLlamarAlMetodoEncode() {
        // Arrange (Organizar)
        String passwordPlano = "password123";
        String passwordEncriptado = "encrypted_password_mock";

        when(passwordEncoder.encode(anyString())).thenReturn(passwordEncriptado);

        // Act (Actuar)
        String resultado = bCryptAdapter.encryptPassword(passwordPlano);

        // Assert
        assertEquals(passwordEncriptado, resultado);

        verify(passwordEncoder).encode(passwordPlano);
    }
}
