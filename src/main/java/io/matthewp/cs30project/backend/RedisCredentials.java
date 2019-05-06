package io.matthewp.cs30project.backend;

import lombok.Getter;
import lombok.NonNull;

/**
 * RedisCredentials
 *
 * Credentials object for the Redis backend.
 */
public final class RedisCredentials extends Credentials {
    @Getter private final String password;

    /**
     * RedisCredentials(String, int, String)
     *
     * Creates a new RedisCredentials object.
     *
     * @param uri Redis URI
     * @param port Redis Port
     * @param password Redis Password
     */
    public RedisCredentials(@NonNull final String uri, @NonNull final int port, @NonNull final String password) {
        super(uri, port);

        this.password = password;
    }

    /**
     * boolean hasPassword()
     *
     * @return True if a password is set, otherwise false.
     */
    public boolean hasPassword() {
        return this.getPassword().length() > 0;
    }
}
