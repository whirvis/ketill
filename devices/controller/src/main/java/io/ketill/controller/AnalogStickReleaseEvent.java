package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is released
 * from a specific direction.
 */
public final class AnalogStickReleaseEvent extends IoFeatureReleaseEvent
        implements AnalogStickEvent {

    private final Direction direction;

    AnalogStickReleaseEvent(@NotNull IoDevice device,
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
