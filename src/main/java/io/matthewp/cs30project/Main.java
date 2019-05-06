package io.matthewp.cs30project;

/**
 * Main
 *
 * Handles starting and initialization of project components.
 */
public final class Main {

    /**
     * void main(final String[])
     *
     * Main entry point for the application.
     *
     * @param args Command Line arguments
     */
    public static void main(final String[] args) {
        //final InputProvider provider = new ConsoleProvider();
        //final InputProvider provider = new RedisProvider(new RedisCredentials("sea1.stacktrace.fun", 6379, ""));
        //provider.start();
        //provider.clean();

        final Config config = new Config();
        config.debug();

        System.exit(0);
    }
}
