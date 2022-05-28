package io.ketill.controller;

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

    AnalogStickReleaseEvent(@NotNull Controller controller,
                            @NotNull AnalogStick stick,
                            @NotNull Direction direction) {
        super(controller, stick);
        this.direction = Objects.requireNonNull(direction,
                "direction cannot be null");
    }

    @Override
    public @NotNull Controller getController() {
        return (Controller) this.getDevice();
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
