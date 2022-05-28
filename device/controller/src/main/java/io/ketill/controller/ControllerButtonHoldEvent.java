package io.ketill.controller;

import io.ketill.pressable.IoFeatureHoldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when a {@link ControllerButton} is held down.
 */
public final class ControllerButtonHoldEvent extends IoFeatureHoldEvent
        implements ControllerButtonEvent {

    ControllerButtonHoldEvent(@NotNull Controller controller,
                              @NotNull ControllerButton button) {
        super(controller, button);
    }

    @Override
    public @NotNull Controller getController() {
        return (Controller) this.getDevice();
    }

    @Override
    public @NotNull ControllerButton getButton() {
        return (ControllerButton) this.getFeature();
    }

}
