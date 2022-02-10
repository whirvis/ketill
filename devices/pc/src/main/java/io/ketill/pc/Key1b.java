package io.ketill.pc;

public class Key1b implements Key1bc {

    public boolean pressed;

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
    public boolean pressed() {
        return this.pressed;
    }

}
