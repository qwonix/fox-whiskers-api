package ru.qwonix.foxwhiskersapi.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

public interface NoPasswordAuthentication extends Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();

    String getUsername();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}

