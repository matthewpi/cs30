package io.matthewp.cs30project.api;

import io.matthewp.cs30project.math.Expression;

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

    /**
     * boolean isCommand(String)
     *
     * Checks if a string is a command.
     *
     * @param input String to check.
     * @return True if string is a command, otherwise false.
     */
    public boolean isCommand(final String input) {
        return input.charAt(0) == '.';
    }

    /**
     * boolean isMathExpression(String)
     *
     * Checks if a string is a mathematical equation.
     *
     * @param input String to check.
     * @return True if string is a command, otherwise false.
     */
    public boolean isMathExpression(final String input) {
        char character = input.charAt(0);

        if(character == '(' || (character >= '0' && character <= '9')) {
            return true;
        }

        // Get the input length.
        int inputLength = input.length();

        // Check if the input length is greater than 2.
        if(inputLength > 2) {
            int endIndex = 0;

            for(int i = 0; i < 5; i++) {
                // Check if the loop index has exceeded the input's length.
                if(i > inputLength - 1) {
                    break;
                }

                // Get the character in the input at the loop's index.
                character = input.charAt(i);

                // Check if the character is a bracket
                if(character == '(') {
                    break;
                }

                // Check if the character is not "A-Z"
                if(!(character >= 'a' && character <= 'z')) {
                    return false;
                }

                // Increment the endIndex variable.
                endIndex++;
            }

            // Check if the FUNCTIONS arraylist contains the matched function.
            if(Expression.FUNCTIONS.contains(input.substring(0, endIndex))) {
                return true;
            }
        }

        return false;
    }
}
