package ru.qwonix.foxwhiskersapi.entity;


import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    READ, WRITE;

    @Override
    public String getAuthority() {
        return name();
    }
}