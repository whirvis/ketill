package io.ketill.pc;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing the cursor of a {@link Mouse}.
 */
public class Cursor extends IoFeature<CursorState> {

    /**
     * @param id the mouse cursor ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public Cursor(@NotNull String id) {
        super(id, CursorState::new);
    }

}
