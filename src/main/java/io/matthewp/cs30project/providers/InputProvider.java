package io.matthewp.cs30project.providers;

/**
 * InputProvider
 *
 * InputProvider is an interface for an input provider,
 * this allows us to have multiple options for getting user input.
 */
public abstract class InputProvider {

    /**
     * void start()
     *
     * Starts the provider's fetching of input.
     */
    public abstract void start();

    /**
     * void clean()
     *
     * Cleans up provider dependencies (API Connections, HTTP Requests, Scanners, Readers, etc)
     * before the application exits.
     */
    public abstract void clean();
}
