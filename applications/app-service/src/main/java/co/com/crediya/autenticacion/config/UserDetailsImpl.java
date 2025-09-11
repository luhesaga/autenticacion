package co.com.crediya.autenticacion.config;

import co.com.crediya.autenticacion.model.usuario.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;
    public UserDetailsImpl(User user) { this.user = user; }

    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getEmail(); }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRolName() != null && !user.getRolName().isBlank()) {
            return Collections.singletonList(new SimpleGrantedAuthority(user.getRolName()));
        }
        return Collections.emptyList();
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
