package io.ketill.controller;

import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Emitted by {@link Controller} when an {@link AnalogStick} is pressed
 * towards a specific direction.
 */
public final class AnalogStickPressEvent extends IoFeaturePressEvent
        implements AnalogStickEvent {

    private final Direction direction;

    AnalogStickPressEvent(@NotNull Controller controller,
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
