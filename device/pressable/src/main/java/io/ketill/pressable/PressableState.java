package io.ketill.pressable;

/**
 * Read-only interface to the state of an I/O feature which can be considered
 * pressed or held down.
 */
public interface PressableState {

    /**
     * Returns if this state is currently pressed.
     *
     * @return {@code true} if this state is currently pressed,
     * {@code false} otherwise.
     */
    boolean isPressed();

    /**
     * Returns if this state is currently held down.
     *
     * @return {@code true} if this state is currently held down,
     * {@code false} otherwise.
     */
    boolean isHeld();

}
