package ru.qwonix.foxwhiskersapi.entity;


import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum Role {
    USER(new HashSet<>(Collections.singletonList(Permission.READ))),
    ADMIN(new HashSet<>(Arrays.asList(Permission.READ, Permission.WRITE)));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<? extends GrantedAuthority> getAuthorities() {
        return getPermissions();
    }
}