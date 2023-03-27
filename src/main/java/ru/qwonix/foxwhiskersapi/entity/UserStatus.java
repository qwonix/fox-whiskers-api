package ru.qwonix.foxwhiskersapi.entity;

/**
 * Represents the status of a {@link User} account.
 */
public enum UserStatus {

    /**
     * Indicates that the user account can be used
     */
    ACTIVE,
    /**
     * Indicates that the user account has been deleted and can no longer be used.
     */
    DELETED
}