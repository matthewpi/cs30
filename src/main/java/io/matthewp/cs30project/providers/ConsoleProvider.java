package io.matthewp.cs30project.providers;

import io.matthewp.cs30project.api.InputProvider;
import io.matthewp.cs30project.math.Expression;
import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * ConsoleProvider
 *
 * InputProvider for taking user input from System.in (Console).
 */
public final class ConsoleProvider extends InputProvider {
    @Getter(AccessLevel.PRIVATE) private final Scanner keyboard;

    /**
     * ConsoleProvider()
     *
     * Creates a new instance of the ConsoleProvider InputProvider.
     */
    public ConsoleProvider() {
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
        String input;
        while(true) {
            input = this.getKeyboard().nextLine().trim();

            // Check if the input should exit the application.
            if(input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting..");
                break;
            }

            // Check if the input is a mathematical expression
            if(this.isMathExpression(input)) {
                System.out.println("Handling input as a math expression.");

                // Create a new Expression object using the input string.
                final Expression expression = new Expression(input);

                // Get the expression's result.
                final BigDecimal result = expression.result();

                // Print the result.
                System.out.println(result);
            }
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
        // Close the input scanner.
        this.getKeyboard().close();
    }
}
