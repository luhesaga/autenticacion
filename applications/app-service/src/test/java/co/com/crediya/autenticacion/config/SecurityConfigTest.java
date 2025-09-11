package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.api.handler.CustomAccessDeniedHandler;
import co.com.crediya.autenticacion.model.usuario.User;
import co.com.crediya.autenticacion.model.usuario.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SecurityConfig.class, SecurityConfigTest.TestBeans.class})
@TestPropertySource(properties = {
        "jwt.secret=TestSecretKey-For-HS512-That-Is-Long-Enough-1234567890-ABCDEFGHIJKLMNOPQRSTUVWXYZ-123456"
})
@Import(SecurityConfigTest.TestBeans.class)
class SecurityConfigTest {

    @Autowired
    ReactiveJwtDecoder jwtDecoder;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ReactiveAuthenticationManager authenticationManager;

    @Autowired
    ReactiveUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SecurityConfig securityConfig;

    private String secret;

    @BeforeEach
    void setUp() {
        secret = "TestSecretKey-For-HS512-That-Is-Long-Enough-1234567890-ABCDEFGHIJKLMNOPQRSTUVWXYZ-123456";
    }

    @Test
    @DisplayName("ReactiveJwtDecoder decodes HS512-signed token with configured secret")
    void reactiveJwtDecoderDecodesHS512() {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        String token = Jwts.builder()
                .setSubject("user@example.com")
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS512)
                .compact();

        var jwt = jwtDecoder.decode(token).block();
        assertThat(jwt).isNotNull();
        assertThat(jwt.getSubject()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("PasswordEncoder is BCrypt and matches encoded passwords")
    void passwordEncoderIsBCrypt() {
        String raw = "s3cr3t";
        String encoded = passwordEncoder.encode(raw);
        assertThat(passwordEncoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("JWT Authentication Converter maps 'roles' claim to SimpleGrantedAuthority as-is")
    void jwtAuthenticationConverterMapsRoles() throws Exception {
        // Build a Jwt with roles claim
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_ASESOR");
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS512")
                .claim("roles", roles)
                .subject("user@example.com")
                .build();

        // Access private method via reflection
        Method m = SecurityConfig.class.getDeclaredMethod("jwtAuthenticationConverter");
        m.setAccessible(true);
        ReactiveJwtAuthenticationConverterAdapter adapter =
                (ReactiveJwtAuthenticationConverterAdapter) m.invoke(securityConfig);

        var authentication = adapter.convert(jwt).block();
        assertThat(authentication).isNotNull();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> auths = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(auths).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_ASESOR");
    }

    @Test
    @DisplayName("ReactiveAuthenticationManager authenticates using encoded password from repository")
    void authenticationManagerAuthenticates() {
        String email = "user@example.com";
        String rawPassword = "p@ssw0rd";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Prepare repository to return a user with encoded password
        User user = User.builder()
                .id(1L)
                .email(email)
                .password(encodedPassword)
                .rolName("ROLE_ADMIN")
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));

        // Authenticate
        Authentication token = new UsernamePasswordAuthenticationToken(email, rawPassword);
        Authentication result = authenticationManager.authenticate(token).block();

        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getName()).isEqualTo(email);
        Set<String> granted = result.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertThat(granted).contains("ROLE_ADMIN");
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        CustomAccessDeniedHandler customAccessDeniedHandler() {
            return Mockito.mock(CustomAccessDeniedHandler.class);
        }

        @Bean
        UserRepository usuarioRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }
}
