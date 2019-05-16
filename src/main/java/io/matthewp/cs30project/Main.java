package io.matthewp.cs30project;

import io.matthewp.cs30project.providers.ConsoleProvider;
import io.matthewp.cs30project.providers.InputProvider;

/**
 * Main
 *
 * Handles the starting and initialization of project components.
 */
public final class Main {

    /**
     * void main(final String[])
     *
     * Main entry point for the application.
     *
     * @param args Command Line arguments
     */
    //@SneakyThrows(IOException.class)
    public static void main(final String[] args) {
        final InputProvider provider = new ConsoleProvider();
        //final InputProvider provider = new RedisProvider(new RedisCredentials("sea1.stacktrace.fun", 6379, ""));
        provider.start();
        provider.clean();

        /*final Config config = new Config();
        config.set("integer", "27017");
        config.set("anotherSection.value2", 3);
        config.set("anotherSection.embeddedSection.value2", 2);
        config.set("anotherSection.embeddedSection.anotherEmbeddedSection.value2", 1);
        config.save();*/

        System.exit(0);
    }
}
