package ru.qwonix.foxwhiskersapi.entity;


import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    READ, WRITE, UPDATE_INFO, TOKEN_REFRESH, TOKEN_LOGOUT;

    @Override
    public String getAuthority() {
        return name();
    }
}