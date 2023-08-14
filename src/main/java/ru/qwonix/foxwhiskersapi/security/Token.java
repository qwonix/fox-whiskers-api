package ru.qwonix.foxwhiskersapi.security;

import ru.qwonix.foxwhiskersapi.entity.Permission;

import java.util.List;

public record Token(String subject, List<Permission> authorities) {
}
