package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.api.handler.CustomAccessDeniedHandler;
import co.com.crediya.autenticacion.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveJwtDecoder jwtDecoder) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/api/v1/usuarios").hasAnyRole("ADMIN", "ASESOR")
                        .pathMatchers("/api/v1/login").permitAll()
                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/webjars/swagger-ui/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler) // Usa handler para errores 403
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(@Value("${jwt.secret}") String secretKey) {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
        // Be explicit about HS512 to match JwtProvider
        return NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByEmail(username)
                .map(UserDetailsImpl::new);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
        // Convert custom "roles" claim to authorities, keeping ROLE_ prefix if present
        delegate.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object rolesClaim = jwt.getClaims().get("roles");
            if (rolesClaim instanceof Collection<?>) {
                @SuppressWarnings("unchecked")
                Collection<Object> roles = (Collection<Object>) rolesClaim;
                return roles.stream()
                        .map(Object::toString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
            // Fallback to standard scope/authorities conversion if present
            JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
            return scopes.convert(jwt);
        });
        return new ReactiveJwtAuthenticationConverterAdapter(delegate);
    }
}
