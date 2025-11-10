package it.unibo.exceptions.fakenetwork.impl;

import it.unibo.exceptions.arithmetic.ArithmeticService;
import it.unibo.exceptions.fakenetwork.api.NetworkComponent;
import it.unibo.exceptions.fakenetwork.api.NetworkException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import static it.unibo.exceptions.arithmetic.ArithmeticService.KEYWORDS;
import static it.unibo.exceptions.arithmetic.ArithmeticUtil.nullIfNumberOrException;

/**
 * A {@link NetworkComponent} mimicking an unstable network.
 */
public final class ServiceBehindUnstableNetwork implements NetworkComponent {
    private final double failProbability;
    private final RandomGenerator randomGenerator;
    private final List<String> commandQueue = new ArrayList<>();

    /**
     * @param failProbability the probability that a network communication fails
     * @param randomSeed random generator seed for reproducibility
     */
    public ServiceBehindUnstableNetwork(final double failProbability, final int randomSeed) {
        if(failProbability < 0 || failProbability >= 1) {
            throw new IllegalArgumentException(
            "Probability must be in range [0, 1), but was: " + failProbability
            );
        }
        this.failProbability = failProbability;
        randomGenerator = new Random(randomSeed);
    }

    /**
     * @param failProbability the probability that a network communication fails
     */
    public ServiceBehindUnstableNetwork(final double failProbability) {
        this(failProbability, 0);
    }

    /**
     * Builds a new service with an unstable network.
     */
    public ServiceBehindUnstableNetwork() {
        this(0.5);
    }

    @Override
    public void sendData(final String data) throws IOException {
        accessTheNetwork(data);
        final var exceptionWhenParsedAsNumber = nullIfNumberOrException(data);
        if (KEYWORDS.contains(data) || exceptionWhenParsedAsNumber == null) {
            commandQueue.add(data);
        } else {
            commandQueue.clear();
            throw new IllegalArgumentException(
                data + " is not a valid keyword (allowed: " + KEYWORDS + "), nor is a number",
                exceptionWhenParsedAsNumber
        );
        }
    }

    @Override
    public String receiveResponse() throws IOException {
        accessTheNetwork(null);
        try {
            return new ArithmeticService(Collections.unmodifiableList(commandQueue)).process();
        } finally {
            commandQueue.clear();
        }
    }

    private void accessTheNetwork(final String message) throws IOException {
        if (randomGenerator.nextDouble() < failProbability) {
            if(message == null) {
                throw new NetworkException();
            }else {
                throw new NetworkException(message);
            }
        }
    }

}
