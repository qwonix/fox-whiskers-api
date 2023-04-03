package ru.qwonix.foxwhiskersapi.security;

import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


/**
 * An {@link org.springframework.security.core.Authentication} implementation that is
 * designed for simple presentation of a JWT access.
 */
@ToString
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String username;
    private final String jwtToken;

    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(String jwtToken, String username, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwtToken = jwtToken;
        this.username = username;
        setAuthenticated(true);
    }


    public static JwtAuthenticationToken unauthenticated(String jwtToken) {
        return new JwtAuthenticationToken(jwtToken);
    }

    public static JwtAuthenticationToken authenticated(String jwtToken, String username, Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(username, jwtToken, authorities);
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
