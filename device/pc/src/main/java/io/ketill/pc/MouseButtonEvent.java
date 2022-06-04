package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to a {@link MouseButton}.
 */
public interface MouseButtonEvent extends MouseEvent {

    /**
     * Returns the button which triggered this event.
     *
     * @return the button which triggered this event.
     */
    @NotNull MouseButton getButton();

}
