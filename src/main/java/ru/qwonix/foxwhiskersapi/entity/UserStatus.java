package ru.qwonix.foxwhiskersapi.entity;

/**
 * Represents the status of a {@link Client} account.
 */
public enum UserStatus {

    /**
     * Indicates that the account can be used
     */
    ACTIVE,
    /**
     * Indicates that the account has been deleted and can no longer be used.
     */
    DELETED
}