package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when a {@link ControllerButton} is pressed.
 */
public final class ControllerButtonPressEvent extends IoFeaturePressEvent
        implements ControllerButtonEvent {

    ControllerButtonPressEvent(@NotNull IoDevice device,
                               @NotNull ControllerButton button) {
        super(device, button);
    }

    @Override
    public @NotNull ControllerButton getButton() {
        return (ControllerButton) this.getFeature();
    }

}
