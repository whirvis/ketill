package io.ketill.controller;

/**
 * Contains the state of a {@link PlayerLed}.
 */
public class Led1i {

    public int number;

    /**
     * @param number the initial player number.
     */
    public Led1i(int number) {
        this.number = number;
    }

    /**
     * Constructs a new {@code PlayerLed1i} with a player number of {@code 1}.
     */
    public Led1i() {
        this(1);
    }

}
