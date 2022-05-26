package io.ketill.controller;

import io.ketill.PlainIoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a rumble motor on a {@link Controller}.
 */
public final class RumbleMotor extends PlainIoFeature<MotorVibration> {

    /**
     * Constructs a new {@code RumbleMotor}.
     *
     * @param id the rumble motor ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public RumbleMotor(@NotNull String id) {
        super(Controller.class, id, MotorVibration::new);
    }

}
