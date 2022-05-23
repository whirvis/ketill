package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events emitted by {@link Mouse}.
 */
public interface MouseEvent {

    /**
     * @return the mouse which emitted this event.
     */
    @NotNull Mouse getMouse();

}
