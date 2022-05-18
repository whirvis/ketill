package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when a {@link ControllerButton} is released.
 */
public final class ControllerButtonReleaseEvent extends IoFeatureReleaseEvent
        implements ControllerButtonEvent {

    ControllerButtonReleaseEvent(@NotNull IoDevice device,
                                 @NotNull ControllerButton button) {
        super(device, button);
    }

    @Override
    public @NotNull ControllerButton getButton() {
        return (ControllerButton) this.getFeature();
    }

}
