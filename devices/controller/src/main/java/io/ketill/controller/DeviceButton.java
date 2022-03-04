package io.ketill.controller;

import io.ketill.Direction;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An I/O feature representing a button on a {@link Controller}.
 *
 * @see AnalogStick
 * @see AnalogTrigger
 */
public class DeviceButton extends IoFeature<Button1b> {

    public final @Nullable Direction direction;

    /**
     * @param id        the button ID.
     * @param direction the direction this button represents. A {@code null}
     *                  value is permitted, and indicates that this button
     *                  does not represent a direction.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public DeviceButton(@NotNull String id, @Nullable Direction direction) {
        super(id, Button1b::new);
        this.direction = direction;
    }

    /**
     * @param id the button ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public DeviceButton(@NotNull String id) {
        this(id, null);
    }

}
