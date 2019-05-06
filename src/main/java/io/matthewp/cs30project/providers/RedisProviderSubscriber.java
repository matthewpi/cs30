package io.matthewp.cs30project.providers;

import io.matthewp.cs30project.api.InputProvider;
import io.matthewp.cs30project.math.Expression;
import lombok.Getter;
import lombok.NonNull;
import redis.clients.jedis.JedisPubSub;

import java.math.BigDecimal;

/**
 * RedisProviderSubscriber
 *
 * ?
 */
public final class RedisProviderSubscriber extends JedisPubSub {
    @Getter private final InputProvider provider;

    public RedisProviderSubscriber(@NonNull final InputProvider provider) {
        this.provider = provider;
    }

    /**
     * onMessage(String, String)
     *
     * Event handler for Redis PubSub inputs.
     *
     * @param chan Redis channel
     * @param message Incoming message
     */
    @Override
    public void onMessage(final String chan, final String message) {
        // Only handle messages that start with "request:"
        if(!message.startsWith("request:")) {
            return;
        }

        // Remove the "request:" from the beginning of the message.
        final String input = message.replaceFirst("request:", "");

        // Check if the input is a mathematical expression
        if(this.getProvider().isMathExpression(input)) {
            // Create a new Expression object using the input string.
            final Expression expression = new Expression(input);

            // Get the expression's result.
            final BigDecimal result = expression.result();

            // Print the result.
            System.out.println(result);
            return;
        }

        // Tell the user they put in an invalid expression.
        System.out.println("Invalid mathematical expression");
    }
}
