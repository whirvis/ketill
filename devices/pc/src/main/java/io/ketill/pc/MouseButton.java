package io.ketill.pc;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a button on a {@link Mouse}.
 */
public class MouseButton extends IoFeature<Click1b> {

    /**
     * @param id the mouse button ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public MouseButton(@NotNull String id) {
        super(id, Click1b::new);
    }

}
