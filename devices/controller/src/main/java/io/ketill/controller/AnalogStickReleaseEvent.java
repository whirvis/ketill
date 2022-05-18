package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is released
 * from a specific direction.
 */
public class AnalogStickReleaseEvent extends IoFeatureReleaseEvent
        implements AnalogStickEvent {

    private final Direction direction;

    /**
     * @param device    the device which emitted this event.
     * @param stick     the analog stick which triggered this event.
     * @param direction the direction of the analog stick.
     * @throws NullPointerException if {@code device}, {@code stick}, or
     *                              {@code direction} are {@code null}.
     */
    public AnalogStickReleaseEvent(@NotNull IoDevice device,
                                   @NotNull AnalogStick stick,
                                   @NotNull Direction direction) {
        super(device, stick);
        this.direction = Objects.requireNonNull(direction,
                "direction cannot be null");
    }

    @Override
    public @NotNull AnalogStick getStick() {
        return (AnalogStick) this.getFeature();
    }

    @Override
    public @NotNull Direction getDirection() {
        return this.direction;
    }

}
