package it.unibo.exceptions.arithmetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static it.unibo.exceptions.arithmetic.ArithmeticUtil.nullIfNumberOrException;
import static java.lang.Double.parseDouble;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A service implementing a simple interpreter for an arithmetic language.
 * Runs expressions composed of numbers and binary operators
 * (see KEYWORDS), respecting operator priorities.
 */
public final class ArithmeticService {

    public static final String TIMES = "times";
    public static final String PLUS = "plus";
    public static final String MINUS = "minus";
    public static final String DIVIDED = "divided";
    public static final Set<String> KEYWORDS = Set.of(TIMES, PLUS, MINUS, DIVIDED);

    private final List<String> commandQueue;

    public ArithmeticService(final List<String> commands) {
        commandQueue = new ArrayList<>(Objects.requireNonNull(commands));
    }

    public String process() {
        try {
            if (commandQueue.isEmpty()) {
                throw new IllegalStateException("No commands sent, no result available");
            }
            while (commandQueue.size() != 1) {
                final var nextMultiplication = commandQueue.indexOf(TIMES);
                final var nextDivision = commandQueue.indexOf(DIVIDED);
                final var nextPriorityOp = nextMultiplication >= 0 && nextDivision >= 0
                    ? min(nextMultiplication, nextDivision)
                    : max(nextMultiplication, nextDivision);
                if (nextPriorityOp >= 0) {
                    computeAt(nextPriorityOp);
                } else {
                    final var nextSum = commandQueue.indexOf(PLUS);
                    final var nextMinus = commandQueue.indexOf(MINUS);
                    final var nextOp = nextSum >= 0 && nextMinus >= 0
                        ? min(nextSum, nextMinus)
                        : max(nextSum, nextMinus);
                    if (nextOp != -1) {
                        if (commandQueue.size() < 3) {
                            throw new IllegalStateException("Inconsistent operation: " + commandQueue);
                        }
                        computeAt(nextOp);
                    } else if (commandQueue.size() > 1) {
                        throw new IllegalStateException("Inconsistent state: " + commandQueue);
                    }
                }
            }
            final var finalResult = commandQueue.get(0);
            final var possibleException = nullIfNumberOrException(finalResult);
            if (possibleException != null) {
                throw new IllegalStateException(
                    "Invalid result of operation: " + finalResult,
                    possibleException
                );
            }
            return finalResult;
        } finally {
            commandQueue.clear();
        }
    }

    private void computeAt(final int operatorIndex) {
        if (operatorIndex == 0) {
            throw new IllegalStateException("Illegal start of operation: " + commandQueue);
        }
        if (commandQueue.size() < 3) {
            throw new IllegalStateException("Not enough operands: " + commandQueue);
        }
        if (commandQueue.size() < operatorIndex + 1) {
            throw new IllegalStateException("Missing right operand: " + commandQueue);
        }
        final var rightOperand = commandQueue.remove(operatorIndex + 1);
        final var leftOperand = commandQueue.remove(operatorIndex - 1);
        
        final double right;
        final double left;
        
        try {
            right = parseDouble(rightOperand);
            left = parseDouble(leftOperand);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                "Expected a number, but got " + leftOperand + " and " + rightOperand + " in " + commandQueue,
                e
            );
        }
        
        final var operand = commandQueue.get(operatorIndex - 1);
        final var result = switch (operand) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case TIMES -> left * right;
            case DIVIDED -> left / right;
            default -> throw new IllegalStateException("Unknown operand " + operand);
        };
        commandQueue.set(operatorIndex - 1, Double.toString(result));
    }
}
