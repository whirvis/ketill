package io.ketill.controller;

import io.ketill.AutonomousField;
import io.ketill.AutonomousState;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Contains the state of a {@link DeviceButton}.
 */
public class ButtonStateZ implements AutonomousState {

    public boolean pressed;

    @AutonomousField
    public boolean held;

    private final DeviceButtonObserver buttonObserver;

    /**
     * @param button   the button which created this state.
     * @param observer the I/O device observer.
     * @throws NullPointerException if {@code button} or {@code observer}
     *                              are {@code null}.
     */
    public ButtonStateZ(@NotNull DeviceButton button,
                        @NotNull IoDeviceObserver observer) {
        Objects.requireNonNull(button, "button cannot be null");
        Objects.requireNonNull(observer, "observer cannot be null");

        this.buttonObserver = new DeviceButtonObserver(button, this, observer);
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
