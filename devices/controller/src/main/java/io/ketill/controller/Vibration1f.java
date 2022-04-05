package io.ketill.controller;

import io.ketill.UserUpdatedField;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link RumbleMotor}.
 */
public class Vibration1f {

    @UserUpdatedField
    private float strength;

    /**
     * @param strength the initial vibration strength.
     */
    public Vibration1f(float strength) {
        this.setStrength(strength);
    }

    /**
     * Constructs a new {@code Vibration1f} with a force of {@code 0.0F}.
     */
    public Vibration1f() {
        this(0.0F);
    }

    /**
     * @param strength the vibration strength to use. This value will be
     *                 capped to a range of {@code 0.0F} to {@code 1.0F}.
     * @return this vibration state.
     */
    @UserUpdatedField
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull Vibration1f setStrength(float strength) {
        /* guarantee a range of 0.0F to 1.0F */
        this.strength = Math.max(0.0F, Math.min(1.0F, strength));
        return this;
    }

    /**
     * @return the current vibration strength, guaranteed to be in range of
     * {@code 0.0F} to {@code 1.0F}.
     */
    @UserUpdatedField
    public float getStrength() {
        return this.strength;
    }

}
