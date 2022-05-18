package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when a {@link ControllerButton} is held down.
 */
public final class ControllerButtonHoldEvent extends IoFeatureHoldEvent
        implements ControllerButtonEvent {

    ControllerButtonHoldEvent(@NotNull IoDevice device,
                              @NotNull ControllerButton button) {
        super(device, button);
    }

    @Override
    public @NotNull ControllerButton getButton() {
        return (ControllerButton) this.getFeature();
    }

}
