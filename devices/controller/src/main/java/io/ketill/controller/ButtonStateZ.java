package io.ketill.controller;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

/**
 * Contains the state of a {@link ControllerButton}.
 */
public final class ButtonStateZ implements AutonomousState {

    /**
     * This should be updated by the adapter to indicate if the button
     * is currently pressed.
     */
    public boolean pressed;

    /**
     * This should <i>not</i> be modified by the adapter.<br>
     * It is updated automatically by the state.
     *
     * @see #pressed
     */
    @AutonomousField
    public boolean held;

    private final ControllerButtonObserver buttonObserver;

    ButtonStateZ(@NotNull ControllerButton button,
                 @NotNull IoDeviceObserver observer) {
        this.buttonObserver = new ControllerButtonObserver(button, this,
                observer);
    }

    /**
     * Constructs a new {@code ButtonStateZ} with no observer. This exists so
     * other classes can easily store the state of a pressable feature.
     */
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
