package io.ketill.controller;

import io.ketill.PlainIoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a Player LED indicator on a
 * {@link Controller}.
 */
public class PlayerLed extends PlainIoFeature<LedState> {

    public final int ledCount;

    /**
     * @param id the player LED ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace; if {@code ledCount} is
     *                                  less than or equal to zero.
     */
    public PlayerLed(@NotNull String id, int ledCount) {
        super(id, LedState::new);
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

}
