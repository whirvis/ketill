package io.ketill.psx;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

/**
 * An I/O feature representing the lightbar of a {@link Ps4Controller}.
 */
public class Lightbar extends IoFeature<Vector4f> {

    /**
     * @param id the lightbar ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public Lightbar(@NotNull String id) {
        super(id, Vector4f::new);
    }

}
