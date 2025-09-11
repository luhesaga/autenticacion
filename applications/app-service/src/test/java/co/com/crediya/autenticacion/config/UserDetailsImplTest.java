package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    @DisplayName("getUsername and getPassword return values from wrapped User")
    void usernameAndPasswordAreDelegated() {
        User domainUser = User.builder()
                .email("john.doe@example.com")
                .password("secret")
                .rolName("ROLE_USER")
                .build();

        UserDetailsImpl details = new UserDetailsImpl(domainUser);

        assertThat(details.getUsername()).isEqualTo("john.doe@example.com");
        assertThat(details.getPassword()).isEqualTo("secret");
    }

    @Test
    @DisplayName("getAuthorities contains a single SimpleGrantedAuthority when rolName is present")
    void authoritiesWithRole() {
        User domainUser = User.builder()
                .email("a@b.c")
                .password("pwd")
                .rolName("ROLE_ADMIN")
                .build();

        UserDetailsImpl details = new UserDetailsImpl(domainUser);

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next()).isInstanceOf(SimpleGrantedAuthority.class);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("getAuthorities is empty when rolName is null")
    void authoritiesWithNullRole() {
        User domainUser = User.builder()
                .email("a@b.c")
                .password("pwd")
                .rolName(null)
                .build();

        UserDetailsImpl details = new UserDetailsImpl(domainUser);
        assertThat(details.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("getAuthorities is empty when rolName is blank")
    void authoritiesWithBlankRole() {
        User domainUser = User.builder()
                .email("a@b.c")
                .password("pwd")
                .rolName("   ")
                .build();

        UserDetailsImpl details = new UserDetailsImpl(domainUser);
        assertThat(details.getAuthorities()).isEmpty();
    }

    @Test
    @DisplayName("Account status flags are all true")
    void accountStatusFlags() {
        User domainUser = User.builder()
                .email("x@y.z")
                .password("pwd")
                .rolName("ROLE_USER")
                .build();

        UserDetailsImpl details = new UserDetailsImpl(domainUser);

        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }
}
