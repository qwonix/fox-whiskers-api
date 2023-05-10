package ru.qwonix.foxwhiskersapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class CodeAuthentication implements Authentication {

    private final String username;
    private final Boolean isAuthenticated;

    private CodeAuthentication(String username, Boolean isAuthenticated) {
        this.username = username;
        this.isAuthenticated = isAuthenticated;
    }

    public static CodeAuthentication authenticated(String username) {
        return new CodeAuthentication(username, true);
    }

    public static CodeAuthentication unauthenticated() {
        return new CodeAuthentication(null, false);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("not supported");
    }

    @Override
    public String getName() {
        return null;
    }
}
