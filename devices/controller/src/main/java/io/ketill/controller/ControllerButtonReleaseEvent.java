package io.ketill.controller;

import io.ketill.pressable.IoFeatureReleaseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when a {@link ControllerButton} is released.
 */
public final class ControllerButtonReleaseEvent extends IoFeatureReleaseEvent
        implements ControllerButtonEvent {

    ControllerButtonReleaseEvent(@NotNull Controller controller,
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
