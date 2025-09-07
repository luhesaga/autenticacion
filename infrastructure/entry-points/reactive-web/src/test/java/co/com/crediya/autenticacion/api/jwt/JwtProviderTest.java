package co.com.crediya.autenticacion.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class JwtProviderTest {

    @Test
    void generateToken_ShouldContainSubjectRolesAndValidTimes() {
        // Arrange
        JwtProvider jwtProvider = new JwtProvider();
        String secret = "M1_Cl4v3_S3cr3t4_P4r4_T3sts_D3b3_S3r_Muy_L4rg4_y_S3gur4_64_Chars__________"; // >= 64 bytes for HS512
        int expirationMs = 5_000; // 5 seconds
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtProvider, "jwtExpirationMs", expirationMs);

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        Authentication auth = new UsernamePasswordAuthenticationToken("jane.doe@example.com", null, authorities);

        long before = System.currentTimeMillis();

        // Act
        String token = jwtProvider.generateToken(auth);

        // Assert
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        long after = System.currentTimeMillis();

        long issuedAtMs = issuedAt.getTime();
        long expirationMsActual = expiration.getTime();
        long toleranceMs = 2000; // account for second-level precision in JWT dates

        assertAll(
                () -> assertThat(claims.getSubject()).isEqualTo("jane.doe@example.com"),
                () -> assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN"),
                () -> assertThat(Math.abs(issuedAtMs - before)).isLessThanOrEqualTo(toleranceMs),
                () -> assertThat(Math.abs(issuedAtMs - after)).isLessThanOrEqualTo(toleranceMs),
                () -> assertThat(Math.abs((issuedAtMs + expirationMs) - expirationMsActual)).isLessThanOrEqualTo(toleranceMs)
        );
    }
}
