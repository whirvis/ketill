package io.ketill.controller;

import io.ketill.PlainIoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a Player LED indicator on a
 * {@link Controller}.
 */
public final class PlayerLed extends PlainIoFeature<LedState> {

    private final int ledCount;

    /**
     * Constructs a new {@code PlayerLed}.
     *
     * @param id       the player LED ID.
     * @param ledCount the amount of LEDs present.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace; if {@code ledCount} is
     *                                  less than or equal to zero.
     */
    public PlayerLed(@NotNull String id, int ledCount) {
        super(Controller.class, id, LedState::new);
        if (ledCount <= 0) {
            throw new IllegalArgumentException("ledCount <= 0");
        }
        this.ledCount = ledCount;
    }

    /**
     * Constructs a new {@code PlayerLed} with the argument for
     * {@code ledCount} being set to {@code 4}.
     *
     * @param id the player LED ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public PlayerLed(@NotNull String id) {
        this(id, 4);
    }

    /**
     * @return the amount of LEDs present.
     */
    public int getLedCount() {
        return this.ledCount;
    }

}
