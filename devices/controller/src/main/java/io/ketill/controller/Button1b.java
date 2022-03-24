package io.ketill.controller;

/**
 * Contains the state of a {@link DeviceButton}.
 */
public class Button1b implements Button1bc {

    public boolean pressed;

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

}
