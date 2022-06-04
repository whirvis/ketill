package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link Mouse}.
 */
public interface MouseEvent {

    /**
     * Returns the mouse which emitted this event.
     *
     * @return the mouse which emitted this event.
     */
    @NotNull Mouse getMouse();

}
