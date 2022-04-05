package io.ketill.controller;

/**
 * Interface to a read-only view of a button's state.
 */
public interface Button1bc {

    /**
     * @return {@code true} if this button is currently pressed,
     * {@code false} otherwise.
     */
    boolean isPressed();

    /**
     * @return {@code true} if this button is currently held down,
     * {@code false} otherwise.
     */
    boolean isHeld();

}
