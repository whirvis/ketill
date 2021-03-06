package io.ketill.psx;

import io.ketill.PlainIoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing the lightbar of a {@link Ps4Controller}.
 */
public final class Lightbar extends PlainIoFeature<LightbarColor> {

    /**
     * Constructs a new {@code Lightbar}.
     *
     * @param id the lightbar ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public Lightbar(@NotNull String id) {
        super(Ps4Controller.class, id, LightbarColor::new);
    }

}
