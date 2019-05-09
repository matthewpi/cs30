package io.matthewp.cs30project;

import io.matthewp.cs30project.dcl.DynamicConfiguration;

/**
 * Config
 *
 * This class handles the project's configuration file.
 */
public final class Config extends DynamicConfiguration {

    /**
     * Config()
     *
     * Creates a new {@link Config} object.
     */
    public Config() {
        super("config.dcl");
    }
}
