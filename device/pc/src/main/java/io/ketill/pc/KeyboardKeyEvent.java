package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to a {@link KeyboardKey}.
 */
public interface KeyboardKeyEvent extends KeyboardEvent {

    /**
     * Returns the key which triggered this event.
     *
     * @return the key which triggered this event.
     */
    @NotNull KeyboardKey getKey();

}
