package io.ketill.pc;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to a {@link MouseCursor}.
 */
public interface MouseCursorEvent extends MouseEvent {

    /**
     * @return the cursor which triggered this event.
     */
    @NotNull MouseCursor getCursor();

}
