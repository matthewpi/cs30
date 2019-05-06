package io.matthewp.cs30project.providers;

import io.matthewp.cs30project.api.InputProvider;
import io.matthewp.cs30project.backend.RedisCredentials;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Scanner;

/**
 * RedisProvider
 *
 * ?
 */
public final class RedisProvider extends InputProvider {
    @Getter(AccessLevel.PRIVATE) private final JedisPool pool;
    @Getter(AccessLevel.PRIVATE) private final JedisPubSub pubSub;
    @Getter(AccessLevel.PRIVATE) private final Scanner keyboard;

    /**
     * RedisProvider({@link RedisCredentials})
     *
     * Creates a new {@link RedisProvider} object.
     *
     * @param credentials Redis Credentials
     */
    public RedisProvider(@NonNull final RedisCredentials credentials) {
        // Check if the credentials has a password.
        if(!credentials.hasPassword()) {
            // Create a new pool with the URI and Port.
            this.pool = new JedisPool(
                    new GenericObjectPoolConfig(),
                    credentials.getUri(),
                    credentials.getPort(),
                    3000
            );
        } else {
            // Create a new pool with the URI, Port, and Password.
            this.pool = new JedisPool(
                    new GenericObjectPoolConfig(),
                    credentials.getUri(),
                    credentials.getPort(),
                    3000,
                    credentials.getPassword()
            );
        }

        // Instate our PubSub subscriber.
        this.pubSub = new RedisProviderSubscriber(this);

        // Create a new input scanner for the application terminal.
        this.keyboard = new Scanner(System.in);
    }

    /**
     * void start()
     *
     * Starts the provider's fetching of input.
     */
    @Override
    public void start() {
        // Prevent this function from being called if the Redis pool is closed.
        if(this.getPool().isClosed()) {
            return;
        }

        // Create a new thread that subscribes to the Redis PubSub and listens for sent messages.
        new Thread(() -> this.getPool().getResource().subscribe(this.getPubSub(), "cs30")).start();

        String input;
        while(true) {
            input = this.getKeyboard().nextLine().trim();

            // Check if the input should exit the application.
            if(input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting..");
                break;
            }

            // Send the message to redis.
            this.getPool().getResource().publish("cs30", "request:" + input.trim());
        }
    }

    /**
     * void clean()
     *
     * Cleans up provider dependencies (API Connections, HTTP Requests, Scanners, Readers, etc)
     * before the application exits.
     */
    @Override
    public void clean() {
        // Check if the Jedis Pool is open.
        if(!this.getPool().isClosed()) {
            // Close the Jedis Pool
            this.getPool().close();
        }

        // Close the input scanner.
        this.getKeyboard().close();
    }
}
