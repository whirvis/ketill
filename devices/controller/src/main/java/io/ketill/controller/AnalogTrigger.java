package io.ketill.controller;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing an analog trigger on a {@link Controller}.
 *
 * @see DeviceButton
 * @see AnalogStick
 */
public class AnalogTrigger extends IoFeature<Trigger1f> {

    /**
     * @param id the analog trigger ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogTrigger(@NotNull String id) {
        super(id, Trigger1f::new);
    }

}
