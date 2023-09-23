package ru.qwonix.foxwhiskersapi.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record Token(String subject, Collection<? extends GrantedAuthority> authorities) {
}
