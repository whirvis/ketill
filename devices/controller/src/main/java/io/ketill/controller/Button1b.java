package io.ketill.controller;

import io.ketill.AdapterUpdatedField;
import io.ketill.pressable.MonitorUpdatedField;

/**
 * Contains the state of a {@link DeviceButton}, one of the directions of an
 * {@link AnalogStick}, or an {@link AnalogTrigger}'s button representation.
 */
public class Button1b implements Button1bc {

    @AdapterUpdatedField("if belongs to DeviceButton")
    @MonitorUpdatedField("if belongs to AnalogStick")
    public boolean pressed;

    @MonitorUpdatedField
    public boolean held;

    /**
     * @param pressed the initial button state.
     */
    public Button1b(boolean pressed) {
        this.pressed = pressed;
    }

    /**
     * Constructs a new {@code Button1b} with {@code pressed} set to
     * {@code false}.
     */
    public Button1b() {
        this(false);
    }

    @Override
    public boolean isPressed() {
        return this.pressed;
    }

    @Override
    public boolean isHeld() {
        return this.held;
    }

}
