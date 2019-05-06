package io.matthewp.cs30project.backend;

import lombok.Getter;
import lombok.NonNull;

/**
 * Credentials
 *
 * Base for any backend credentials.
 */
public abstract class Credentials {
    @Getter private final String uri;
    @Getter private final int port;

    /**
     * Credentials(String, int)
     *
     * Creates a new Credentials object.
     *
     * @param uri URI
     * @param port Port
     */
    public Credentials(@NonNull final String uri, final int port) {
        this.uri = uri;
        this.port = port;
    }
}
