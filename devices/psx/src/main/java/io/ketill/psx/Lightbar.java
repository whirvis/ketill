package io.ketill.psx;

import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class Lightbar extends IoFeature<Vector4f> {

    public Lightbar(@NotNull String id) {
        super(id, Vector4f::new);
    }

}
