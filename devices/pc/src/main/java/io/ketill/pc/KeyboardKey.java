package io.ketill.pc;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a key on a {@link Keyboard}.
 */
public class KeyboardKey extends IoFeature<Key1b> {

    /**
     * @param id the keyboard key ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public KeyboardKey(@NotNull String id) {
        super(id, Key1b::new);
    }

}
