package io.ketill.pc;

import io.ketill.AdapterUpdatedField;
import io.ketill.pressable.MonitorUpdatedField;

/**
 * Contains the state of a {@link KeyboardKey}.
 */
public class Key1b implements Key1bc {

    @AdapterUpdatedField
    public boolean pressed;

    @MonitorUpdatedField
    public boolean held;

    /**
     * @param pressed the initial key state.
     */
    public Key1b(boolean pressed) {
        this.pressed = pressed;
    }

    /**
     * Constructs a new {@code Key1b} with {@code pressed} set to
     * {@code false}.
     */
    public Key1b() {
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
