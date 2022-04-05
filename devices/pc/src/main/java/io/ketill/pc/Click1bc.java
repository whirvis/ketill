package io.ketill.pc;

/**
 * Interface to a read-only view of a mouse button's state.
 */
public interface Click1bc {

    /**
     * @return {@code true} if this mouse button is currently clicked,
     * {@code false} otherwise.
     */
    boolean isClicked();

    /**
     * @return {@code true} if this mouse button is currently held down,
     * {@code false} otherwise.
     */
    boolean isHeld();

}
