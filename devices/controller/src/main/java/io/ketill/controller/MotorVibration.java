package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link RumbleMotor}.
 */
public class MotorVibration {

    private float strength;

    /**
     * @param strength the vibration strength to use. This value will be
     *                 capped to a range of {@code 0.0F} to {@code 1.0F}.
     * @return this vibration state.
     */
    public @NotNull MotorVibration setStrength(float strength) {
        /* guarantee a range of 0.0F to 1.0F */
        this.strength = Math.max(0.0F, Math.min(1.0F, strength));
        return this;
    }

    /**
     * @return the current vibration strength, guaranteed to be in range of
     * {@code 0.0F} to {@code 1.0F}.
     */
    public float getStrength() {
        return this.strength;
    }

}
