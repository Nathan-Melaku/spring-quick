package et.nate.backend.authentication;

import et.nate.backend.data.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serial;
import java.util.Collection;
import java.util.Map;

public class AppUserDetail implements UserDetails, OAuth2User {

    @Serial
    private static final long serialVersionUID = 1L;

    private final User user;

    public AppUserDetail(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // TODO add Expiration and Locking
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getRoles().stream()
                .noneMatch(role -> role.getName().equals(AuthConstants.DEFAULT_ROLE));
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}
