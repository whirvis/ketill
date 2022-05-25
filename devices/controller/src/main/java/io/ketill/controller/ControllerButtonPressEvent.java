package io.ketill.controller;

import io.ketill.pressable.IoFeaturePressEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link Controller} when a {@link ControllerButton} is pressed.
 */
public final class ControllerButtonPressEvent extends IoFeaturePressEvent
        implements ControllerButtonEvent {

    ControllerButtonPressEvent(@NotNull Controller controller,
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
