package io.ketill.psx;

import io.ketill.PlainIoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing the lightbar of a {@link Ps4Controller}.
 */
public class Lightbar extends PlainIoFeature<LightbarColor> {

    /**
     * @param id the lightbar ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public Lightbar(@NotNull String id) {
        super(id, LightbarColor::new);
    }

}
