package io.ketill.pc;

/**
 * Interface to a read-only view of a keyboard key's state.
 */
public interface Key1bc {

    /**
     * @return {@code true} if this keyboard key is currently pressed,
     * {@code false} otherwise.
     */
    boolean pressed();

}
