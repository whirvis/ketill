package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to a {@link MouseButton}.
 */
public interface MouseButtonEvent {

    /**
     * @return the button which triggered this event.
     */
    @NotNull MouseButton getButton();

}
