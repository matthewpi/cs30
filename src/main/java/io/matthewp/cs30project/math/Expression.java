package io.matthewp.cs30project.math;

import lombok.AccessLevel;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expression
 *
 * Takes a string input and attempts to process it as a mathematical expression.
 */
public final class Expression {
    // FUNCTIONS stores a list of valid functions.
    public static final List<String> FUNCTIONS = new ArrayList<>();

    // Add the default functions to our FUNCTIONS list.
    static {
        FUNCTIONS.add("sqrt");
        FUNCTIONS.add("cbrt");
        FUNCTIONS.add("round");
        FUNCTIONS.add("ceil");
        FUNCTIONS.add("floor");
        FUNCTIONS.add("sin");
        FUNCTIONS.add("cos");
        FUNCTIONS.add("tan");
        FUNCTIONS.add("fib");
    }

    @Getter private final String input;
    @Getter(AccessLevel.PRIVATE) private int position;
    @Getter(AccessLevel.PRIVATE) private int character;
    @Getter(AccessLevel.PRIVATE) private BigDecimal result;
    @Getter private boolean parsed;

    /**
     * Expression(String)
     *
     * Creates a new Expression object.
     */
    public Expression(final String input) {
        this.input = input;
        this.position = -1;
        this.character = -1;
        this.result = null;
        this.parsed = false;
    }

    /**
     * BigDecimal result()
     *
     * @return Cached result or result from parse().
     */
    public BigDecimal result() {
        // Check if we have parsed the input already.
        if(this.isParsed()) {
            return this.getResult();
        }

        return this.parse();
    }

    /**
     * BigDecimal parse()
     *
     * Parses the mathematical equation and returns the result.
     *
     * @return Expression result.
     */
    private BigDecimal parse() {
        // Check if we have already parsed the input.
        if(this.isParsed()) {
            return this.getResult();
        }

        // Go to the first character.
        this.next();

        // Start parsing the input.
        this.result = this.parseExpression();

        // Check if we finished parsing without going through the entire input.
        if(this.getPosition() < this.getInput().length()) {
            throw new RuntimeException("Unexpected '" + (char) this.getCharacter() + "' at position " + this.getPosition() + ".");
        }

        // Update the parsed variable so we can pull the result again later.
        this.parsed = true;

        return this.getResult();
    }

    /**
     * BigDecimal parseFactor()
     *
     * Primary logic for our expression parsing algorithm, handles brackets, numbers, functions, and exponents.
     *
     * @return double
     */
    private BigDecimal parseFactor() {
        // Skip over addition.
        if(this.isNext('+')) {
            return this.parseFactor();
        }

        // Skip over subtraction.
        if(this.isNext('-')) {
            return this.parseFactor().negate();
        }

        final int startPosition = this.position;
        BigDecimal x;

        // Check if we need to parse a set of parentheses.
        if(this.isNext('(')) {
            // Handle the content within the brackets as a new expression.
            x = this.parseExpression();

            // Find the ending bracket.
            this.isNext(')');
        // Check if the character is numeric or a decimal point.
        } else if((this.character >= '0' && this.character <= '9') || this.character == '.') {
            // Loop until we finish finding all consecutive "0-9 and ." characters.
            while((this.character >= '0' && this.character <= '9') || this.character == '.') {
                this.next();
            }

            // Keep parsing the input as a double
            x = new BigDecimal(this.input.substring(startPosition, this.position));
        } else if(this.character >= 'a' && this.character <= 'z') {
            // Loop until we finish finding all consecutive "A-Z" characters.
            while(this.character >= 'a' && this.character <= 'z') {
                this.next();
            }

            // Get the function name.
            String functionName = this.getInput().substring(startPosition, this.getPosition());

            // Parse the inside of the function.
            x = this.parseFactor();

            switch(functionName) {
                // Square Root function, Ex: "sqrt(16)" will return "4".
                case "sqrt":
                    x = BigDecimal.valueOf(Math.sqrt(x.doubleValue()));
                    break;
                // Cube Root function, Ex: "cbrt(27)" will return "3".
                case "cbrt":
                    x = BigDecimal.valueOf(Math.cbrt(x.doubleValue()));
                    break;
                // Round function, Ex: "round(1.4)" will return "1", Ex: "round(1.6)" will return "2".
                case "round":
                    x = BigDecimal.valueOf(Math.round(x.doubleValue()));
                    break;
                // Ceil function (Round Up), Ex: "ceil(1.2)" will return "2".
                case "ceil":
                    x = BigDecimal.valueOf(Math.ceil(x.doubleValue()));
                    break;
                // Floor function (Round Up), Ex: "floor(1.8)" will return "1".
                case "floor":
                    x = BigDecimal.valueOf(Math.floor(x.doubleValue()));
                    break;
                // Sine function (Trigonometry)
                case "sin":
                    x = BigDecimal.valueOf(Math.sin(x.doubleValue()));
                    break;
                // Cosine function (Trigonometry)
                case "cos":
                    x = BigDecimal.valueOf(Math.cos(x.doubleValue()));
                    break;
                // Tangent function (Trigonometry)
                case "tan":
                    x = BigDecimal.valueOf(Math.tan(x.doubleValue()));
                    break;
                // Fibonacci Index
                case "fib":
                    x = new BigDecimal(fib(x.intValue()));
                    break;
                // An unimplemented function was used, throw an error.
                default:
                    throw new RuntimeException("Invalid function \"" + functionName + "\" at position " + startPosition + ".");
            }
        // We don't know how to handle this character, throw an error.
        } else {
            throw new RuntimeException("Unexpected '" + (char) this.getCharacter() + "' at position " + this.getPosition() + ".");
        }

        // Check if the next valid character is a '^' (exponent symbol)
        if(this.isNext('^')) {
            x = BigDecimal.valueOf(Math.pow(x.doubleValue(), this.parseFactor().doubleValue()));
        }

        return x;
    }

    /**
     * BigDecimal parseExpression()
     *
     * Parses a mathematical expression and handles addition and subtraction.
     *
     * @return double
     */
    private BigDecimal parseExpression() {
        BigDecimal x = this.parseTerm();

        // Loop until we are done handling addition and subtraction.
        while(true) {
            // Find the next addition symbol.
            if(this.isNext('+')) {
                x = x.add(this.parseTerm());
            // Find the next subtraction symbol.
            } else if(this.isNext('-')) {
                x = x.subtract(this.parseTerm());
            // Return the result.
            } else {
                return x;
            }
        }
    }

    /**
     * BigDecimal parseTerm()
     *
     * Parses a mathematical term and handles multiplication and division.
     *
     * @return double
     */
    private BigDecimal parseTerm() {
        BigDecimal x = this.parseFactor();

        // Loop until we are done handling multiplication and division.
        while(true) {
            // Find the next multiplication symbol.
            if(this.isNext('*')) {
                x = x.multiply(this.parseFactor());
            // Find the next division symbol.
            } else if(this.isNext('/')) {
                x = x.divide(this.parseFactor());
            // Return the result.
            } else {
                return x;
            }
        }
    }

    /**
     * void next()
     *
     * Updates the `this.character` variable with the next character in the `this.input` variable.
     */
    private void next() {
        // Check if the next position is less than the input's length.
        if(++this.position < this.getInput().length()) {
            // Update our character variable to the next character in the input.
            this.character = this.getInput().charAt(this.getPosition());
        } else {
            // We are at the end of our input, set character to -1.
            this.character = -1;
        }
    }

    /**
     * boolean isNext(int)
     *
     * Checks if the next non-space character matches the function's argument.
     *
     * @return True if character was the next valid character, otherwise false.
     */
    private boolean isNext(final int character) {
        // Loop until we find a character that isn't a space.
        while(this.getCharacter() == ' ') {
            this.next();
        }

        // Check if the current character matches what we are searching for.
        if(this.getCharacter() == character) {
            this.next();
            return true;
        }

        return false;
    }

    // CACHE stores indexes of already computed fibonacci sequence numbers.
    private static final Map<Integer, BigInteger> CACHE = new HashMap<>();

    /**
     * BigInteger fib(int)
     *
     * Computes an a number in the fibonacci sequence for the specified index.
     *
     * @param number Index in the fibonacci sequence
     * @return Fibonacci Number
     */
    private BigInteger fib(final int number) {
        if(number < 3) {
            return BigInteger.valueOf(1);
        }

        int indexOne = number - 1;
        BigInteger numberOne;
        if(CACHE.containsKey(indexOne)) {
            numberOne = CACHE.get(indexOne);
        } else {
            numberOne = this.fib(indexOne);
            CACHE.put(indexOne, numberOne);
        }

        int indexTwo = number - 2;
        BigInteger numberTwo;
        if(CACHE.containsKey(indexTwo)) {
            numberTwo = CACHE.get(indexTwo);
        } else {
            numberTwo = this.fib(indexTwo);
            CACHE.put(indexTwo, numberTwo);
        }

        return numberOne.add(numberTwo);
    }
}
