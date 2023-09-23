package ru.qwonix.foxwhiskersapi.entity;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
public enum Role {
    CLIENT(Set.of(Permission.READ)),
    ADMIN(Set.of(Permission.READ, Permission.WRITE)),
    INCOMPLETE_REGISTRATION(Set.of(Permission.UPDATE_INFO));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<? extends GrantedAuthority> getAuthorities() {
        return getPermissions();
    }
}