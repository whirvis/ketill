package io.ketill.controller;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link ControllerButton}.
 */
public final class ButtonStateZ implements AutonomousState {

    public boolean pressed;

    @AutonomousField
    public boolean held;

    private final ControllerButtonObserver buttonObserver;

    ButtonStateZ(@NotNull ControllerButton button,
                 @NotNull IoDeviceObserver observer) {
        this.buttonObserver = new ControllerButtonObserver(button, this,
                observer);
    }

    public ButtonStateZ() {
        this.buttonObserver = null;
    }

    @Override
    public void update() {
        if (buttonObserver != null) {
            buttonObserver.poll();
        }
    }

}
