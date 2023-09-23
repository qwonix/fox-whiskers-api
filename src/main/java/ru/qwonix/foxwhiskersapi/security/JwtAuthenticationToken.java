package ru.qwonix.foxwhiskersapi.security;

import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * An {@link org.springframework.security.core.Authentication} implementation that is
 * designed for simple presentation of a JWT access.
 */
@ToString
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwtToken;
    private UserDetails userDetails;

    public JwtAuthenticationToken(String jwtToken) {
        super(null);
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(String jwtToken, UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwtToken = jwtToken;
        this.userDetails = userDetails;
        setAuthenticated(true);
    }


    public static JwtAuthenticationToken unauthenticated(String jwtToken) {
        return new JwtAuthenticationToken(jwtToken);
    }

    public static JwtAuthenticationToken authenticated(String jwtToken, UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(jwtToken, userDetails, authorities);
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
