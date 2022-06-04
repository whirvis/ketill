package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link Keyboard}.
 */
public interface KeyboardEvent {

    /**
     * Returns the keyboard which emitted this event.
     *
     * @return the keyboard which emitted this event.
     */
    @NotNull Keyboard getKeyboard();

}
