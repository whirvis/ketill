package io.ketill.controller;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a rumble motor on a {@link Controller}.
 */
public class RumbleMotor extends IoFeature<Vibration1f> {

    /**
     * @param id the rumble motor ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public RumbleMotor(@NotNull String id) {
        super(id, Vibration1f::new);
    }

}
